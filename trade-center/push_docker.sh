#  usage: ./push_docker.sh dev 1

crName="trade-center"
prefixVersion=0.0
compileEnv=$1
#version=$2
version=`date +"%Y%m%d%H%M%S"`

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

repoName="gmall-repo"
if test "$compileEnv" = "prod"
then
  repoName="gmall-repo"
fi

#编译打包
mvn clean install -Dmaven.test.skip -U -P ${compileEnv}

docker login --username=myth_test@test.aliyunid.com --password myth1234 registry.cn-hangzhou.aliyuncs.com

imageName=registry.cn-hangzhou.aliyuncs.com/${repoName}/${crName}:${version}.${compileEnv}

docker build ./ --pull=true -t ${imageName}
docker push ${imageName}
