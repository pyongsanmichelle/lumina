FROM mcr.microsoft.com/devcontainers/base:ubuntu

# JDK/Node/Tooling
RUN apt-get update && apt-get install -y \
    curl unzip git \
 && rm -rf /var/lib/apt/lists/*

# Temurin JDK 21
RUN mkdir -p /opt/java && \
    curl -L -o /tmp/jdk.tar.gz https://github.com/adoptium/temurin21-binaries/releases/latest/download/OpenJDK21U-jdk_x64_linux_hotspot.tar.gz && \
    tar -xzf /tmp/jdk.tar.gz -C /opt/java --strip-components=1 && \
    rm /tmp/jdk.tar.gz
ENV JAVA_HOME=/opt/java
ENV PATH="$JAVA_HOME/bin:$PATH"

# Node 20 + corepack
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
 && apt-get update && apt-get install -y nodejs \
 && corepack enable \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /workspaces/repo
