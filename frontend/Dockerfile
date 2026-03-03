# Stage 1: Build Angular application
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the Angular application for Docker environment
RUN npm run build -- --configuration docker

# Stage 2: Serve with http-server
FROM node:20-alpine

WORKDIR /app

# Install http-server globally
RUN npm install -g serve

# Copy built application from builder stage
COPY --from=builder /app/dist/frontend/browser ./dist

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Start http-server
CMD ["serve", "-s", "dist", "-l", "8080"]

