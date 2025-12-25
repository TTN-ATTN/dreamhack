FROM node:22-slim

ENV PORT=3000 \
    FLAG=DH{FAKE_FLAG} \
    SESSION_SECRET=fake_secret

RUN apt-get update && \
    apt-get install -y --no-install-recommends wget unzip ca-certificates

# install google chrome
RUN wget -O ./google-chrome-stable.deb https://mirror.cs.uchicago.edu/google-chrome/pool/main/g/google-chrome-stable/google-chrome-stable_141.0.7390.54-1_amd64.deb && \
    apt-get install -y ./google-chrome-stable.deb && \
    rm ./google-chrome-stable.deb && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY deploy/ /app/
RUN chown -R node:node /app
USER node
RUN npm ci

EXPOSE $PORT

CMD ["node", "app.js"]