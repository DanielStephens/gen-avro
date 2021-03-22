DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

./install.sh $1
mvn deploy:deploy-file \
      -Dfile=target/gen-avro.jar \
      -DrepositoryId=clojars \
      -Durl=https://clojars.org/repo \
      -DpomFile=pom.xml
