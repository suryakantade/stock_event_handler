package com.shortener.urlshortener.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortener.urlshortener.common.model.RequestContext;
import com.shortener.urlshortener.common.model.UrlShortenerResponseObject;
import com.shortener.urlshortener.common.model.UrlShortenerStatusCode;
import com.shortener.urlshortener.common.util.GenericUtility;
import com.shortener.urlshortener.v1.entity.ShortUrl;
import com.shortener.urlshortener.v1.model.UrlShortenerModel;
import com.shortener.urlshortener.v1.repository.ShortUrlRepository;
import com.shortener.urlshortener.v1.service.UrlShortenerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service("com.shortener.urlshortener.common.v1.service.impl.UrlShortenerServiceImpl")
@Slf4j
public class UrlShortenerServiceImpl implements UrlShortenerService {


  @Autowired
  @Qualifier("com.shortener.urlshortener.v1.repository.ShortUrlRepository")
  private ShortUrlRepository shortUrlRepository;

  //todo: check collision based on generated token and clientId

  @Override
  public UrlShortenerResponseObject<UrlShortenerModel> shortenUrl(RequestContext context,
      UrlShortenerModel urlShortenerModel) {
    log.info("shortening url for context: {} and urlShortenerModel: {}", context,
        urlShortenerModel);
    UrlShortenerResponseObject<UrlShortenerModel> responseObject =
        new UrlShortenerResponseObject<>(UrlShortenerStatusCode.DATA_VALIDATION_FAILED);
    if (urlShortenerModel.isValid()) {
      ShortUrl shortUrl =
          ShortUrl.builder().redirectedUrl(urlShortenerModel.getRedirectedUrl()).acccessCount(0)
              .expieryTime(new Timestamp(urlShortenerModel.getExpieryTime()))
              .isSingleAccess(urlShortenerModel.getIsSingleAccess()).clientId(context.getClientId())
              .token(GenericUtility.getRandomToken(5)).build();
      shortUrl = shortUrlRepository.save(shortUrl);
      urlShortenerModel.setId(shortUrl.getId());
      urlShortenerModel.setToken(shortUrl.getToken());
      responseObject.setResponseObject(urlShortenerModel);
      responseObject.setStatus(UrlShortenerStatusCode.SUCCESS);
    } else {
      log.error("data validation failed for urlShortenerModel: {}", urlShortenerModel);
    }
    return responseObject;
  }

  @Override
  public UrlShortenerModel validateAndFetchShortenedDetails(String token) {
    log.info("fetching and validating shortened url info for token: {}", token);
    UrlShortenerModel urlShortenerModel = null;
    Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByToken(token);
    ShortUrl shortUrl = shortUrlOptional.orElse(null);
    if (shortUrl != null && BooleanUtils.isNotTrue(shortUrl.getIsExpired())) {
      urlShortenerModel =
          UrlShortenerModel.builder().redirectedUrl(shortUrl.getRedirectedUrl()).build();
    }else{
      log.error("No short url found for token : {}", token);
    }
    return urlShortenerModel;
  }

  public static void main(String[] args) throws JsonProcessingException {
    UrlShortenerModel urlShortenerModel = UrlShortenerModel.builder().redirectedUrl(
        "https://www.quora.com/unanswered/What-are-some-of-the-funniest-questions-asked-on-Quora")
        .expieryTime(1573768584L).build();
    System.out.println(new ObjectMapper().writeValueAsString(urlShortenerModel));
  }
}
