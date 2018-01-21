FROM clojure
LABEL maintainer="dra1n"
COPY . /usr/src/app
WORKDIR /usr/src/app                                 
EXPOSE 8080                                          
CMD ["lein", "run"]
