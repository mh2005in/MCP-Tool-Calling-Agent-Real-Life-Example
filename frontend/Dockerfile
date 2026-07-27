# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies from the lockfile for reproducible builds
COPY package.json package-lock.json ./
RUN npm ci

# Build the production bundle. The 'production' configuration swaps in
# environment.prod.ts (apiUrl='/api/v1'), so the SPA calls the API same-origin
# and nginx proxies it to the backend container.
COPY . .
RUN npm run build -- --configuration production

# ---- Runtime stage (nginx serves static files + proxies /api) ----
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/immigration-automation/browser /usr/share/nginx/html

EXPOSE 80
