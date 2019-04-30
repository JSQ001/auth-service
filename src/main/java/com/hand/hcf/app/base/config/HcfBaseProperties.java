package com.hand.hcf.app.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hcf")
public class HcfBaseProperties {
    private final Storage storage = new Storage();
    private final SSO sso = new SSO();

    public SSO getSso() {
        return sso;
    }

    public Storage getStorage() {
        return storage;
    }

    public static class Storage {
        private final OSS oss = new OSS();
        private final FTP ftp = new FTP();
        private String mode;
        private Long resizeThreshold=1000000L;

        public String getMode() {
            return mode;
        }
        public FTP getFtp() {
            return ftp;
        }
        public void setMode(String mode) {
            this.mode = mode;
        }


        public OSS getOss() {
            return oss;
        }

        public Long getResizeThreshold() {
            return resizeThreshold;
        }

        public void setResizeThreshold(Long resizeThreshold) {
            this.resizeThreshold = resizeThreshold;
        }
        @Data
        public static class FTP {
            private String url;

            private int port;

            private String username;

            private String password;

            private String staticUrl;

            private String privateKey;
            /**
             * 统一文件夹名称 后续根据此值做nginx代理
             */
            private String directoryName = "upload";
            /**
             * 保存的路径，如果为空则取ftp用户的默认路径,确保该目录在服务器上存在，并且给用户授权
             */
            private String uploadPath;

        }

        public static class OSS {
            private final Client client = new Client();
            private final Bucket bucket = new Bucket();
            private final LegacyClient legacyClient = new LegacyClient();
            private final LegacyBucket legacyBucket = new LegacyBucket();

            private String endpoint;

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            private String filehost;

            public String getFilehost() {
                return filehost;
            }

            public void setFilehost(String filehost) {
                this.filehost = filehost;
            }

            public Client getClient() {
                return client;
            }

            public Bucket getBucket() {
                return bucket;
            }

            public LegacyBucket getLegacyBucket() {
                return legacyBucket;
            }

            public LegacyClient getLegacyClient() {
                return legacyClient;
            }

            public static class Bucket {
                private String name;
                private String url;
                private String staticName;
                private String staticUrl;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getStaticName() {
                    return staticName;
                }

                public void setStaticName(String staticName) {
                    this.staticName = staticName;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getStaticUrl() {
                    return staticUrl;
                }

                public void setStaticUrl(String staticUrl) {
                    this.staticUrl = staticUrl;
                }
            }

            public static class LegacyBucket {
                private String name;
                private String url;
                private String staticName;
                private String staticUrl;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getStaticName() {
                    return staticName;
                }

                public void setStaticName(String staticName) {
                    this.staticName = staticName;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getStaticUrl() {
                    return staticUrl;
                }

                public void setStaticUrl(String staticUrl) {
                    this.staticUrl = staticUrl;
                }
            }

            public static class Client {
                private String id;
                private String secret;
                private int maxErrorRetry;
                private int maxConnections;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public int getMaxErrorRetry() {
                    return maxErrorRetry;
                }

                public void setMaxErrorRetry(int maxErrorRetry) {
                    this.maxErrorRetry = maxErrorRetry;
                }

                public int getMaxConnections() {
                    return maxConnections;
                }

                public void setMaxConnections(int maxConnections) {
                    this.maxConnections = maxConnections;
                }
            }

            public static class LegacyClient {
                private String id;
                private String secret;
                private int maxErrorRetry;
                private int maxConnections;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public int getMaxErrorRetry() {
                    return maxErrorRetry;
                }

                public void setMaxErrorRetry(int maxErrorRetry) {
                    this.maxErrorRetry = maxErrorRetry;
                }

                public int getMaxConnections() {
                    return maxConnections;
                }

                public void setMaxConnections(int maxConnections) {
                    this.maxConnections = maxConnections;
                }
            }
        }
    }

    public static class SSO {
        private String callback;
        private String baseURL;

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }

        public String getBaseURL() {
            return baseURL;
        }

        public void setBaseURL(String baseURL) {
            this.baseURL = baseURL;
        }
    }
}
