package org.springframework.boot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationBannerPrinter.class */
class SpringApplicationBannerPrinter {
    static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";
    static final String BANNER_IMAGE_LOCATION_PROPERTY = "spring.banner.image.location";
    static final String DEFAULT_BANNER_LOCATION = "banner.txt";
    static final String[] IMAGE_EXTENSION = {"gif", "jpg", "png"};
    private static final Banner DEFAULT_BANNER = new SpringBootBanner();
    private final ResourceLoader resourceLoader;
    private final Banner fallbackBanner;

    SpringApplicationBannerPrinter(ResourceLoader resourceLoader, Banner fallbackBanner) {
        this.resourceLoader = resourceLoader;
        this.fallbackBanner = fallbackBanner;
    }

    Banner print(Environment environment, Class<?> sourceClass, Log logger) {
        Banner banner = getBanner(environment);
        try {
            logger.info(createStringFromBanner(banner, environment, sourceClass));
        } catch (UnsupportedEncodingException ex) {
            logger.warn("Failed to create String for banner", ex);
        }
        return new PrintedBanner(banner, sourceClass);
    }

    Banner print(Environment environment, Class<?> sourceClass, PrintStream out) {
        Banner banner = getBanner(environment);
        banner.printBanner(environment, sourceClass, out);
        return new PrintedBanner(banner, sourceClass);
    }

    private Banner getBanner(Environment environment) {
        Banners banners = new Banners();
        banners.addIfNotNull(getImageBanner(environment));
        banners.addIfNotNull(getTextBanner(environment));
        if (banners.hasAtLeastOneBanner()) {
            return banners;
        }
        if (this.fallbackBanner != null) {
            return this.fallbackBanner;
        }
        return DEFAULT_BANNER;
    }

    private Banner getTextBanner(Environment environment) {
        String location = environment.getProperty("spring.banner.location", "banner.txt");
        Resource resource = this.resourceLoader.getResource(location);
        try {
            if (resource.exists() && !resource.getURL().toExternalForm().contains("liquibase-core")) {
                return new ResourceBanner(resource);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private Banner getImageBanner(Environment environment) {
        String location = environment.getProperty(BANNER_IMAGE_LOCATION_PROPERTY);
        if (StringUtils.hasLength(location)) {
            Resource resource = this.resourceLoader.getResource(location);
            if (resource.exists()) {
                return new ImageBanner(resource);
            }
            return null;
        }
        for (String ext : IMAGE_EXTENSION) {
            Resource resource2 = this.resourceLoader.getResource("banner." + ext);
            if (resource2.exists()) {
                return new ImageBanner(resource2);
            }
        }
        return null;
    }

    private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        banner.printBanner(environment, mainApplicationClass, new PrintStream(baos));
        String charset = environment.getProperty("spring.banner.charset", "UTF-8");
        return baos.toString(charset);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationBannerPrinter$Banners.class */
    private static class Banners implements Banner {
        private final List<Banner> banners;

        private Banners() {
            this.banners = new ArrayList();
        }

        void addIfNotNull(Banner banner) {
            if (banner != null) {
                this.banners.add(banner);
            }
        }

        boolean hasAtLeastOneBanner() {
            return !this.banners.isEmpty();
        }

        @Override // org.springframework.boot.Banner
        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
            for (Banner banner : this.banners) {
                banner.printBanner(environment, sourceClass, out);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationBannerPrinter$PrintedBanner.class */
    private static class PrintedBanner implements Banner {
        private final Banner banner;
        private final Class<?> sourceClass;

        PrintedBanner(Banner banner, Class<?> sourceClass) {
            this.banner = banner;
            this.sourceClass = sourceClass;
        }

        @Override // org.springframework.boot.Banner
        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
            this.banner.printBanner(environment, sourceClass != null ? sourceClass : this.sourceClass, out);
        }
    }
}
