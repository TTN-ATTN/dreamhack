package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/AsyncRestOperations.class */
public interface AsyncRestOperations {
    RestOperations getRestOperations();

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(String url, Object... uriVariables) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException;

    ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Object... uriVariables) throws RestClientException;

    ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVariables) throws RestClientException;

    ListenableFuture<URI> postForLocation(URI url, @Nullable HttpEntity<?> request) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, @Nullable HttpEntity<?> request, Class<T> responseType) throws RestClientException;

    ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Object... uriVariables) throws RestClientException;

    ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVariables) throws RestClientException;

    ListenableFuture<?> put(URI url, @Nullable HttpEntity<?> request) throws RestClientException;

    ListenableFuture<?> delete(String url, Object... uriVariables) throws RestClientException;

    ListenableFuture<?> delete(String url, Map<String, ?> uriVariables) throws RestClientException;

    ListenableFuture<?> delete(URI url) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object... uriVariables) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException;

    <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException;

    <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException;

    <T> ListenableFuture<T> execute(URI url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException;
}
