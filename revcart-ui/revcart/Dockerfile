# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies first (layer-cached unless package.json changes)
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Copy source and build with production environment
COPY . .
RUN npm run build -- --configuration=production

# ─── Stage 2: Serve ───────────────────────────────────────────────────────────
FROM nginx:1.27-alpine

# Remove default nginx static content
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app from stage 1
COPY --from=builder /app/dist/revcart /usr/share/nginx/html

# Custom nginx config for Angular (SPA — all unknown paths → index.html)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
