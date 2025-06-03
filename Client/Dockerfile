FROM node:23-alpine

# RUN apk add --no-cache make gcc g++ python3

WORKDIR /app

COPY package.json ./
COPY package-lock.json ./

RUN npm install
RUN npm audit fix

COPY . .

RUN npm install -g serve

RUN npm run build

EXPOSE 4173

CMD ["serve", "-s", "dist", "-l", "4173"]
