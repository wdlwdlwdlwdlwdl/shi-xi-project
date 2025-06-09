compileEnv=$1

cd ../trade-platform
./mvn_deploy.sh

cd ../trade-center
./push_docker.sh ${compileEnv}