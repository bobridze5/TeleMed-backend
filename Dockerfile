## =========================
## Stage 1: Build
## =========================
#FROM eclipse-temurin:21-jdk-alpine AS build
#
#CMD ["gradle"]
#
#ENV GRADLE_HOME=/opt/gradle
#
## Создание пользователя gradle и настройка кэша
#RUN set -o errexit -o nounset \
#    && echo "Adding gradle user and group" \
#    && addgroup --system --gid 1000 gradle \
#    && adduser --system --ingroup gradle --uid 1000 --shell /bin/ash gradle \
#    && mkdir /home/gradle/.gradle \
#    && chown -R gradle:gradle /home/gradle \
#    && chmod -R o+rwx /home/gradle \
#    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
#    && ln -s /home/gradle/.gradle /root/.gradle
#
#VOLUME /home/gradle/.gradle
#
#WORKDIR /home/gradle
#
## Установка утилит и VCS
#RUN set -o errexit -o nounset \
#    && apk add --no-cache make curl wget tar breezy py3-tzlocal git git-lfs mercurial subversion unzip bash \
#    && echo "Testing common utilities and VCSes" \
#    && which awk && which curl && which cut && which grep && which gunzip && which sha256sum \
#    && which sed && which tar && which tr && which unzip && which wget \
#    && which git && which git-lfs && which hg && which svn
#
## Установка Gradle
#ENV GRADLE_VERSION=9.2.0
#ARG GRADLE_DOWNLOAD_SHA256=df67a32e86e3276d011735facb1535f64d0d88df84fa87521e90becc2d735444
#RUN set -o errexit -o nounset \
#    && echo "Downloading Gradle" \
#    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
#    && echo "Checking Gradle download hash" \
#    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
#    && echo "Installing Gradle" \
#    && unzip gradle.zip \
#    && rm gradle.zip \
#    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
#    && ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle
#
#USER gradle
#
#RUN set -o errexit -o nounset \
#    && echo "Testing Gradle installation" \
#    && gradle --version
#
## Копируем проект и собираем jar
#WORKDIR /home/gradle/project
#COPY --chown=gradle:gradle . .
#RUN gradle bootJar --no-daemon
#
## =========================
## Stage 2: Runtime
## =========================
#FROM eclipse-temurin:21-jdk-alpine
#
#WORKDIR /app
#
## Копируем готовый jar из образа сборки
#COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
#
## Запуск Spring Boot
#ENTRYPOINT ["java","-jar","app.jar"]


FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


