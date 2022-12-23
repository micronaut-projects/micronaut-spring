/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.web.reactive;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.handler.ssl.SslHandler;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Micronaut implementation of {@link org.springframework.http.server.reactive.ServerHttpRequest}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class MicronautServerHttpRequest extends AbstractServerHttpRequest {
    private final HttpRequest<?> request;
    private final ChannelResolver channelResolver;

    /**
     * Default constructor.
     * @param request The request to adapt
     * @param channelResolver The channel resolver
     */
    public MicronautServerHttpRequest(
            HttpRequest<?> request,
            ChannelResolver channelResolver) {
        super(request.getUri(), null, initHeaders(request));
        this.request = request;
        this.channelResolver = channelResolver;
    }

    private static HttpHeaders initHeaders(HttpRequest<?> request) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        final io.micronaut.http.HttpHeaders micronautHeaders = request.getHeaders();
        micronautHeaders.forEach(entry -> {
            final String key = entry.getKey();
            final List<String> value = entry.getValue();
            httpHeaders.addAll(key, value);
        });
        return httpHeaders;
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        final Cookies cookies = request.getCookies();
        MultiValueMap<String, HttpCookie> cookieMultiValueMap = new LinkedMultiValueMap<>();
        cookies.forEach((s, cookie) -> {
            final HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
            cookieMultiValueMap.set(s, httpCookie);
        });
        return cookieMultiValueMap;
    }

    @Override
    protected SslInfo initSslInfo() {

        final Optional<Channel> channel = channelResolver.resolveChannel(request);
        if (channel.isPresent()) {

            SslHandler sslHandler = channel.get().pipeline().get(SslHandler.class);
            if (sslHandler != null) {
                SSLSession session = sslHandler.engine().getSession();
                return new DefaultSslInfo(session);
            }
        }
        return null;
    }

    @Override
    public <T> T getNativeRequest() {
        return (T) request;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod().name());
    }

    @Override
    public String getMethodValue() {
        return request.getMethod().name();
    }

    @SuppressWarnings("SubscriberImplementation")
    @Override
    public reactor.core.publisher.Flux<DataBuffer> getBody() {
        final Optional<Channel> opt = channelResolver.resolveChannel(request);
        if (opt.isPresent()) {
            final Channel channel = opt.get();
            final NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(channel.alloc());

            final Optional<HttpContentProcessor> httpContentProcessor = channelResolver.resolveContentProcessor(request);

            if (httpContentProcessor.isPresent()) {

                final HttpContentProcessor processor = httpContentProcessor.get();
                /*
                return Flux.from(subscriber -> processor.subscribe(new Subscriber<ByteBufHolder>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(ByteBufHolder byteBufHolder) {
                        subscriber.onNext(nettyDataBufferFactory.wrap(byteBufHolder.content()));
                    }

                    @Override
                    public void onError(Throwable t) {
                        subscriber.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                }));*/
                return Flux.empty();
            }
        }

        return Flux.empty();
    }

    /**
     * Default implementation of {@link SslInfo}.
     *
     * @author Rossen Stoyanchev
     * @since 5.0.2
     */
    final class DefaultSslInfo implements SslInfo {

        @Nullable
        private final String sessionId;

        @Nullable
        private final X509Certificate[] peerCertificates;

        /**
         * Default constructor.
         * @param session The SSLSession
         */
        DefaultSslInfo(SSLSession session) {
            Assert.notNull(session, "SSLSession is required");
            this.sessionId = initSessionId(session);
            this.peerCertificates = initCertificates(session);
        }

        @Override
        @Nullable
        public String getSessionId() {
            return this.sessionId;
        }

        @Override
        @Nullable
        public X509Certificate[] getPeerCertificates() {
            return this.peerCertificates;
        }

        @Nullable
        private String initSessionId(SSLSession session) {
            byte [] bytes = session.getId();
            if (bytes == null) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String digit = Integer.toHexString(b);
                if (digit.length() < 2) {
                    sb.append('0');
                }
                if (digit.length() > 2) {
                    digit = digit.substring(digit.length() - 2);
                }
                sb.append(digit);
            }
            return sb.toString();
        }

        @Nullable
        private X509Certificate[] initCertificates(SSLSession session) {
            Certificate[] certificates;
            try {
                certificates = session.getPeerCertificates();
            } catch (Throwable ex) {
                return null;
            }

            List<X509Certificate> result = new ArrayList<>(certificates.length);
            for (Certificate certificate : certificates) {
                if (certificate instanceof X509Certificate) {
                    result.add((X509Certificate) certificate);
                }
            }
            return (!result.isEmpty() ? result.toArray(new X509Certificate[0]) : null);
        }

    }

}
