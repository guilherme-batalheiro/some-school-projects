FROM ubuntu:22.04

# To install Maven dependecies
COPY backend/pom.xml .

# To install NPM dependencies
# COPY frontend/package.json .

ENV TZ=Europe/Lisbon
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt update -y && apt upgrade -y && \
    apt install -y --no-install-recommends openjdk-11-jdk postgresql postgresql-client maven gawk wget python2 sudo && \
    apt install -y --no-install-recommends libgtk2.0-0 libgtk-3-0 libgbm-dev libnotify-dev libgconf-2-4 libnss3 libxss1 libasound2 libxtst6 xauth xvfb && \
    wget -O - https://deb.nodesource.com/setup_16.x | bash - && apt install -y nodejs && \
    mvn dependency:go-offline -B -Ptest,test-int && rm -f pom.xml && \
    /etc/init.d/postgresql start && \
    sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';" && \
    sudo -u postgres psql -c "CREATE DATABASE tutordb;" && \
    /etc/init.d/postgresql stop && \
    wget -O /bin/cover2cover.py https://raw.githubusercontent.com/rix0rrr/cover2cover/master/cover2cover.py && \
    apt autoremove -y && apt-get autoclean && apt clean && \
    rm -fr ~/.wget-hsts /var/cache/apt /var/lib/apt /var/log/* /usr/share/icons

# Takes too much space
#   npm install && rm -f package.json && \
