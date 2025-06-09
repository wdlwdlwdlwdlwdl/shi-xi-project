#  usage: ./push_docker.sh dev 1

crName="front-manager"
prefixVersion=0.0
compileEnv=$1
settings=$2
version=$(date +"%Y%m%d%H%M%S")


if test -z "$compileEnv"
then
  echo "need compileEnv[dev|prod]"
  exit 2
fi

if test -z "$version"
then
  echo "need version"
  exit 2
fi

git pull

#编译打包
if test -z "$settings"
then
mvn clean install -Dmaven.test.skip -U -P ${compileEnv}
else
  mvn clean install -Dmaven.test.skip -U -P ${compileEnv} --settings ${settings}
fi

docker login --username=myth_test@test.aliyunid.com --password myth1234 registry.cn-hangzhou.aliyuncs.com

imageName=registry.cn-hangzhou.aliyuncs.com/gmall-repo/${crName}:${prefixVersion}.${version}.${compileEnv}

docker build ./ --pull=true -t ${imageName}
docker push ${imageName}
