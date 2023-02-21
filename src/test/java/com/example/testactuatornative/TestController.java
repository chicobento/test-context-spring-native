package com.example.testactuatornative;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
	private static final Log log = LogFactory.getLog(TestController.class);

	@RequestMapping("/")
	public String home() {
		return "test controller compatible home";
	}

	@GetMapping("/public-access/headers")
	public Map<String, Object> publicHeaders(ServerWebExchange exchange) {
		Map<String, Object> result = new HashMap<>();
		result.put("headers", exchange.getRequest().getHeaders().toSingleValueMap());
		return result;
	}

	@RequestMapping(value = "/public-access/headers-servlet", method = { RequestMethod.GET, RequestMethod.POST },
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> publicHeaders(@RequestHeader HttpHeaders headers) {
		Map<String, Object> result = new HashMap<>();
		result.put("headers", headers.toSingleValueMap());
		return result;
	}

	@GetMapping("/webjars/swagger-ui/index.html")
	public Mono<String> swaggerUi() {
		return Mono.just("ok");
	}

	@GetMapping("/v3/api-docs/api-web")
	public Mono<String> apiDocs() {
		return Mono.just("ok");
	}

	@GetMapping("/v3/swagger-no-reactive")
	public Mono<String> swaggerNoReal() {
		return Mono.just("ok");
	}

	@RequestMapping(path = "/headers", method = { RequestMethod.GET, RequestMethod.POST },
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> headers(@RequestHeader Map<String, String> headers) {
		Map<String, Object> result = new HashMap<>();
		result.put("headers", headers);
		return result;
	}

	@RequestMapping(path = "/delay/{sec}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String, Object>> get(ServerWebExchange exchange, @PathVariable int sec)
			throws InterruptedException {
		int delay = Math.min(sec, 10);
		return Mono.just(get(exchange)).delayElement(Duration.ofSeconds(delay));
	}

	@RequestMapping(path = "/anything/{anything}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> anything(ServerWebExchange exchange, @PathVariable(required = false) String anything) {
		return get(exchange);
	}

	@RequestMapping(path = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> get(ServerWebExchange exchange) {
		if (log.isDebugEnabled()) {
			log.debug("test /get");
		}
		HashMap<String, Object> result = new HashMap<>();
		HashMap<String, String> params = new HashMap<>();
		exchange.getRequest().getQueryParams().forEach((name, values) -> {
			params.put(name, values.get(0));
		});
		result.put("args", params);
		result.put("headers", getHeaders(exchange));
		return result;
	}

	@RequestMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String, Object>> postFormData(@RequestBody Mono<MultiValueMap<String, Part>> parts) {
		// StringDecoder decoder = StringDecoder.allMimeTypes(true);
		return parts.flux().flatMap(map -> Flux.fromIterable(map.values())).flatMap(Flux::fromIterable)
				.filter(part -> part instanceof FilePart).reduce(new HashMap<String, Object>(), (files, part) -> {
					MediaType contentType = part.headers().getContentType();
					long contentLength = part.headers().getContentLength();
					files.put(part.name(), "data:" + contentType + ";base64," + contentLength); // TODO:
																								// get
																								// part
																								// data
					return files;
				}).map(files -> Collections.singletonMap("files", files));
	}

	@RequestMapping(path = "/post", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String, Object>> postUrlEncoded(ServerWebExchange exchange) throws IOException {
		return post(exchange, null);
	}

	@RequestMapping(path = "/post", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String, Object>> post(ServerWebExchange exchange, @RequestBody(required = false) String body)
			throws IOException {
		HashMap<String, Object> ret = new HashMap<>();
		ret.put("headers", getHeaders(exchange));
		ret.put("data", body);
		HashMap<String, Object> form = new HashMap<>();
		ret.put("form", form);

		return exchange.getFormData().flatMap(map -> {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				for (String value : entry.getValue()) {
					form.put(entry.getKey(), value);
				}
			}
			return Mono.just(ret);
		});
	}

	@RequestMapping("/status/{status}")
	public ResponseEntity<String> status(@PathVariable int status) {
		return ResponseEntity.status(status).body("Failed with " + status);
	}

	public Map<String, String> getHeaders(ServerWebExchange exchange) {
		return exchange.getRequest().getHeaders().toSingleValueMap();
	}

}
