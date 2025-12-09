FROM node:20-alpine@sha256:eabac870db94f7342d6c33560d6613f188bbcf4bbe1f4eb47d5e2a08e1a37722

ENV USER chall
ENV PORT 3000

RUN adduser -D -g "" $USER

COPY --chown=root:root ./deploy /abn_gallery
COPY --chown=root:$USER ./flag /flag

WORKDIR /abn_gallery
RUN npm install

USER $USER
EXPOSE $PORT

CMD ["node", "app.js"]