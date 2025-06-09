
import com.aliyun.gts.gmall.framework.generator.AbstractGenerator;

public class GenerateControllerTest extends AbstractGenerator {
    public static void main(String[] args) {
        GenerateControllerTest generator = new GenerateControllerTest();
        generator.generate();
    }

    /** controller层单测生成开关和配置项 start **/
    @Override
    public boolean needControllerTestng() {
        return true;
    }
    @Override
    public String controllerDefinitionPackageFullName() {
        return "com.aliyun.gts.gmall.platform.trade.http.generated";
    }
    @Override
    public String controllerMavenModuleName() {
        return "order/order-rest-controller";
    }
    /** controller层单测生成开关和配置项 end **/


    /** 以下不用管，用来满足抽象类需求的固定实现 **/
    @Override
    public String restApiStarterMavenModuleName() {
        return "order/order-rest-controller";
    }

    @Override
    public String apiMavenModuleName() {
        return "order/order-api";
    }

    @Override
    public String apiDefinitionFacadePackageFullName() {
        return "com.aliyun.gts.gmall.platform.trade.api.facade";
    }

    @Override
    public String serverMavenModuleName() {
        return "order/order-server";
    }

    @Override
    public boolean needServer() {
        return true;
    }

    @Override
    public boolean needApiDoc() {
        return false;
    }

    @Override
    public boolean needTestng() {
        return false;
    }

    @Override
    public boolean needServiceTestng() {
        return false;
    }

    @Override
    public String apiImplementationFacadePackageFullName() {
        return "com.aliyun.gts.gmall.platform.trade.api.facade";
    }

    public String serviceDefinitionPackageFullName() {
        return "com.aliyun.gts.gmall.platform.trade.api.facade.service";
    }
}
