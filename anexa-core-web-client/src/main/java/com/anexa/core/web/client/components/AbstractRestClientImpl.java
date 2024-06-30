package com.anexa.core.web.client.components;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract public class AbstractRestClientImpl implements RestClient {

	abstract public RestTemplate getRestTemplate();

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- HTTP GET METHODS
	// ------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public <T, ID> ResponseEntity<T> get(
	    String resourcePath,
	    Class<T> responseType,
	    ID id) {
		return get(resourcePath, getHttpHeaders(), responseType, id);
	}

	@Override
	public <T, ID> ResponseEntity<T> get(
	    String resourcePath,
	    HttpHeaders headers,
	    Class<T> responseType,
	    ID id) {
		return getOneQuery(resourcePath, "{id}", headers, responseType, id);
	}

	@Override
	public <T> ResponseEntity<T> getOneQuery(
	    String resourcePath,
	    String query,
	    Class<T> responseType,
	    Object... uriVariables) {
		return getOneQuery(resourcePath, query, getHttpHeaders(), responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> getOneQuery(
	    String resourcePath,
	    String query,
	    HttpHeaders headers,
	    Class<T> responseType,
	    Object... uriVariables) {
		HttpEntity<?> request = new HttpEntity<>(headers);

		val url = buildUrl(resourcePath, query);
		log.debug("{} {}", HttpMethod.GET.toString(), url);
		val result = getRestTemplate().exchange(url, HttpMethod.GET, request, responseType, uriVariables);
		return result;
	}

	@Override
	public <T> ResponseEntity<T[]> getAllQuery(
	    String resourcePath,
	    String query,
	    Class<T[]> responseType,
	    Object... uriVariables) {
		return getAllQuery(resourcePath, query, getHttpHeaders(), responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T[]> getAllQuery(
	    String resourcePath,
	    String query,
	    HttpHeaders headers,
	    Class<T[]> responseType,
	    Object... uriVariables) {
		HttpEntity<?> request = new HttpEntity<>(headers);

		val url = buildUrl(resourcePath, query);
		log.debug("{} {}", HttpMethod.GET.toString(), url);
		val result = getRestTemplate().exchange(url, HttpMethod.GET, request, responseType, uriVariables);
		return result;
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- HTTP POST, PUT, DELETE METHODS
	// ------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public <T> ResponseEntity<T> post(
	    String resourcePath,
	    Object model,
	    Class<T> responseType,
	    Object... uriVariables) {
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<?> request = new HttpEntity<>(mapModelToRequestBody(model), headers);

		log.debug("{} {}", HttpMethod.POST.toString(), resourcePath);
		val result = getRestTemplate().exchange(resourcePath, HttpMethod.POST, request, responseType, uriVariables);
		return result;
	}

	@Override
	public <T> ResponseEntity<T> put(
	    String resourcePath,
	    Object model,
	    Class<T> responseType,
	    Object... uriVariables) {
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<?> request = new HttpEntity<>(mapModelToRequestBody(model), headers);

		log.debug("{} {}", HttpMethod.PUT.toString(), resourcePath);
		val result = getRestTemplate().exchange(resourcePath, HttpMethod.PUT, request, responseType, uriVariables);
		return result;
	}

	@Override
	public <T> ResponseEntity<T> patch(
	    String resourcePath,
	    Object model,
	    Class<T> responseType,
	    Object... uriVariables) {
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<?> request = new HttpEntity<>(mapModelToRequestBody(model), headers);

		log.debug("{} {}", HttpMethod.PATCH.toString(), resourcePath);
		val result = getRestTemplate().exchange(resourcePath, HttpMethod.PATCH, request, responseType, uriVariables);
		return result;
	}

	@Override
	public <ID> void delete(
	    String resourcePath,
	    ID id) {

		log.debug("{} {}/{}", HttpMethod.DELETE.toString(), resourcePath, id);
		getRestTemplate().delete(resourcePath, id);
	}

	@Override
	public <ID> void delete(
	    String resourcePath,
	    ID id,
	    int version) {

		log.debug("{} {}/{}", HttpMethod.DELETE.toString(), resourcePath, id);
		getRestTemplate().delete(resourcePath, id, version);
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// --
	// ------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public HttpHeaders getHttpHeaders() {
		return getHttpHeaders(getDefaultMediaType());
	}

	@Override
	public HttpHeaders getHttpHeaders(
	    MediaType mediaType) {
		return getHttpHeaders(mediaType, getDefaultHeaders());
	}

	@Override
	public HttpHeaders getHttpHeaders(
	    MediaType mediaType,
	    MultiValueMap<String, String> values) {
		HttpHeaders result = new HttpHeaders();
		result.setContentType(mediaType);

		if (values != null) {
			result.addAll(values);
		}

		return result;
	}

	@Override
	public MultiValueMap<String, String> getDefaultHeaders() {
		return new LinkedMultiValueMap<String, String>();
	}

	protected MediaType getDefaultMediaType() {
		return MediaType.APPLICATION_JSON;
	}

	public static String buildUrl(
	    String resourcePath,
	    String query) {
		val sb = new StringBuilder();
		sb.append(resourcePath);

		query = query.trim();

		if (!query.isEmpty()) {
			char c = query.charAt(0);
			if (c != '?' && c != '/') {
				sb.append('/');
			}
			sb.append(query);
		}

		return sb.toString();
	}

	protected <T> Object mapModelToRequestBody(
	    T model) {
		return model;
	}
}
