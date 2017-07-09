package com.dmzadorin.telegram.bot.fxbot.common.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.*;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.updatesreceivers.ExponentialBackOff;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.telegram.telegrambots.Constants.SOCKET_TIMEOUT;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class FxBotSession implements BotSession {
    private static final String LOGTAG = "BOTSESSION";
    private ExecutorService threadPool;
    private ConcurrentLinkedDeque<Update> receivedUpdates;
    private ObjectMapper objectMapper;

    private volatile boolean running;
    private volatile int lastReceivedUpdate;

    private LongPollingBot callback;
    private String token;
    private DefaultBotOptions options;

    @Inject
    public FxBotSession() {
    }

    @Override
    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("Session already running");
        }

        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(callback.getBotUsername()).build();
        threadPool = Executors.newFixedThreadPool(2, factory);
        receivedUpdates = new ConcurrentLinkedDeque<>();
        objectMapper = new ObjectMapper();
        running = false;

        running = true;

        lastReceivedUpdate = 0;
        FxUpdateReader task = new FxUpdateReader();
        task.start();
        threadPool.submit(task);
        HandlerRunnable handlerRunnable = new HandlerRunnable();
        threadPool.submit(handlerRunnable);
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            throw new IllegalStateException("Session already stopped");
        }

        running = false;

        threadPool.shutdownNow();

        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (callback != null) {
            callback.onClosing();
        }
    }

    @Override
    public void setOptions(BotOptions options) {
        if (this.options != null) {
            throw new InvalidParameterException("BotOptions has already been set");
        }
        this.options = (DefaultBotOptions) options;
    }

    @Override
    public void setToken(String token) {
        if (this.token != null) {
            throw new InvalidParameterException("Token has already been set");
        }
        this.token = token;
    }

    @Override
    public void setCallback(LongPollingBot callback) {
        if (this.callback != null) {
            throw new InvalidParameterException("Callback has already been set");
        }
        this.callback = callback;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private class FxUpdateReader implements Runnable, UpdatesReader {
        private CloseableHttpClient httpclient;
        private ExponentialBackOff exponentialBackOff;
        private RequestConfig requestConfig;

        @Override
        public synchronized void start() {
            httpclient = HttpClientBuilder.create()
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setConnectionTimeToLive(70, TimeUnit.SECONDS)
                    .setMaxConnTotal(100)
                    .build();
            requestConfig = options.getRequestConfig();
            exponentialBackOff = options.getExponentialBackOff();

            if (exponentialBackOff == null) {
                exponentialBackOff = new ExponentialBackOff();
            }

            if (requestConfig == null) {
                requestConfig = RequestConfig.copy(RequestConfig.custom().build())
                        .setSocketTimeout(1000)
                        .setConnectTimeout(1000)
                        .setConnectionRequestTimeout(1000).build();
            }
        }

        @Override
        public void run() {
            while (running) {
                try {
                    GetUpdates request = buildGetUpdatesRequest();
                    HttpPost httpPost = buildHttpRequest(request);
                    processRequest(request, httpPost);
                } catch (InterruptedException e) {
                    if (!running) {
                        receivedUpdates.clear();
                    }
                    BotLogger.debug(LOGTAG, e);
                } catch (Exception global) {
                    BotLogger.severe(LOGTAG, global);
                    try {
                        synchronized (this) {
                            this.wait(exponentialBackOff.nextBackOffMillis());
                        }
                    } catch (InterruptedException e) {
                        if (!running) {
                            receivedUpdates.clear();
                        }
                        BotLogger.debug(LOGTAG, e);
                    }
                }
            }
            close();
            BotLogger.debug(LOGTAG, "Reader thread has being closed");
        }

        private GetUpdates buildGetUpdatesRequest() {
            GetUpdates request = new GetUpdates()
                    .setLimit(100)
                    .setTimeout(ApiConstants.GETUPDATES_TIMEOUT)
                    .setOffset(lastReceivedUpdate + 1);

            if (options.getAllowedUpdates() != null) {
                request.setAllowedUpdates(options.getAllowedUpdates());
            }
            return request;
        }

        private void processRequest(GetUpdates request, HttpPost httpPost) throws IOException, InterruptedException {
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity ht = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(ht);
                String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                if (response.getStatusLine().getStatusCode() >= 500) {
                    BotLogger.warn(LOGTAG, responseContent);
                    synchronized (this) {
                        this.wait(500);
                    }
                } else {
                    processSuccessRequest(request, responseContent);
                }
            } catch (InvalidObjectException | TelegramApiRequestException e) {
                BotLogger.severe(LOGTAG, e);
            }
        }

        private HttpPost buildHttpRequest(GetUpdates request) throws JsonProcessingException {
            String url = options.getBaseUrl() + token + "/" + GetUpdates.PATH;
            //http client
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
            return httpPost;
        }

        private void processSuccessRequest(GetUpdates request, String responseContent) throws TelegramApiRequestException, InterruptedException {
            List<Update> updates = Collections.emptyList();
            try {
                updates = request.deserializeResponse(responseContent);
            } catch (JSONException e) {
                BotLogger.severe(responseContent, LOGTAG, e);
            }

            exponentialBackOff.reset();

            if (updates.isEmpty()) {
                synchronized (this) {
                    this.wait(500);
                }
            } else {
                updates.removeIf(x -> x.getUpdateId() < lastReceivedUpdate);
                lastReceivedUpdate = updates.parallelStream()
                        .map(Update::getUpdateId)
                        .max(Integer::compareTo)
                        .orElse(0);
                receivedUpdates.addAll(updates);

                synchronized (receivedUpdates) {
                    receivedUpdates.notifyAll();
                }
            }

        }

        private void close() {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    BotLogger.warn(LOGTAG, e);
                }
            }
        }
    }

    private class HandlerRunnable implements Runnable, UpdatesHandler {
        @Override
        public void run() {
            while (running) {
                try {
                    Update update = receivedUpdates.pollLast();
                    if (update == null) {
                        synchronized (receivedUpdates) {
                            receivedUpdates.wait();
                            update = receivedUpdates.pollLast();
                            if (update == null) {
                                continue;
                            }
                        }
                    }
                    callback.onUpdateReceived(update);
                } catch (InterruptedException e) {
                    BotLogger.debug(LOGTAG, e);
                } catch (Exception e) {
                    BotLogger.severe(LOGTAG, e);
                }
            }
            BotLogger.debug(LOGTAG, "Handler thread has being closed");
        }
    }
}
