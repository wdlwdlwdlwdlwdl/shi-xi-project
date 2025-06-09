package com.aliyun.gts.gmall.center.trade.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.*;

/**
 * 多语言配置比较工具类，在项目中无用
 */
public class PropertiesI18nUtil {

    public static void main(String[] args) throws Exception {

        String jsonArray = "[\n" +
                "    {\n" +
                "        \"key\": \"normal.order\",\n" +
                "        \"en\": \"Normal Order\",\n" +
                "        \"ru\": \"Обычный заказ\",\n" +
                "        \"kk\": \"Қарапайым тапсырыс\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.on.behalf\",\n" +
                "        \"en\": \"Order on Behalf\",\n" +
                "        \"ru\": \"Заказ от имени другого лица\",\n" +
                "        \"kk\": \"Басқа тұлғаның атынан тапсырыс беру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"unsupported.order.type\",\n" +
                "        \"en\": \"Unsupported Order Type\",\n" +
                "        \"ru\": \"Неподдерживаемый тип заказа\",\n" +
                "        \"kk\": \"Қолдау көрсетілмейтін тапсырыс түрі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"placeholder\",\n" +
                "        \"en\": \"Placeholder\",\n" +
                "        \"ru\": \"Заполнитель\",\n" +
                "        \"kk\": \"Толтырғыш\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"unused\",\n" +
                "        \"en\": \"Unused\",\n" +
                "        \"ru\": \"Неиспользуемый\",\n" +
                "        \"kk\": \"Пайдаланылмаған\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"redeemed\",\n" +
                "        \"en\": \"Redeemed\",\n" +
                "        \"ru\": \"Выкуплен\",\n" +
                "        \"kk\": \"Сатып алынды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"physical.goods.order\",\n" +
                "        \"en\": \"Physical Goods Order\",\n" +
                "        \"ru\": \"Заказ физических товаров\",\n" +
                "        \"kk\": \"Жеке тауарларға тапсырыс беру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"e.voucher.order\",\n" +
                "        \"en\": \"Electronic Voucher Order\",\n" +
                "        \"ru\": \"Заказ электронного ваучера\",\n" +
                "        \"kk\": \"Электрондық ваучерге тапсырыс беру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"error.during.signing\",\n" +
                "        \"en\": \"Error During Signing Process\",\n" +
                "        \"ru\": \"Ошибка при процессе подписания\",\n" +
                "        \"kk\": \"Қол қою процесінде қате\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"incorrect.encoding.set\",\n" +
                "        \"en\": \"Incorrect Encoding Set\",\n" +
                "        \"ru\": \"Неверная кодировка\",\n" +
                "        \"kk\": \"Қате кодтау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"current.encoding.set\",\n" +
                "        \"en\": \"Current Encoding Set\",\n" +
                "        \"ru\": \"Текущая кодировка\",\n" +
                "        \"kk\": \"Ағымдағы кодтау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"exceed.item.limit\",\n" +
                "        \"en\": \"Exceeds Limit of Concurrent Items\",\n" +
                "        \"ru\": \"Превышен лимит одновременно размещаемых товаров\",\n" +
                "        \"kk\": \"Бір уақытта орналастырылатын тауарлардың лимиті асып кетті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"below.min.quantity\",\n" +
                "        \"en\": \"Below Minimum Quantity\",\n" +
                "        \"ru\": \"Меньше минимального количества\",\n" +
                "        \"kk\": \"Минималды саннан аз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"above.max.quantity.limit\",\n" +
                "        \"en\": \"Above Maximum Quantity Limit\",\n" +
                "        \"ru\": \"Превышен лимит максимального количества\",\n" +
                "        \"kk\": \"Максималды сан лимитінен асып кетті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"product.order.prohibited\",\n" +
                "        \"en\": \"Product Order Prohibited\",\n" +
                "        \"ru\": \"Заказ на продукт запрещен\",\n" +
                "        \"kk\": \"Өнімге тапсырыс беруге тыйым салынады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"incorrect.product.price\",\n" +
                "        \"en\": \"Incorrect Product Price\",\n" +
                "        \"ru\": \"Неверная цена продукта\",\n" +
                "        \"kk\": \"Өнімнің бағасы қате\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"insufficient.user.points\",\n" +
                "        \"en\": \"Insufficient User Points\",\n" +
                "        \"ru\": \"Недостаточно баллов пользователя\",\n" +
                "        \"kk\": \"Пайдаланушы ұпайлары жеткіліксіз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"bid.document.ordering.forbidden\",\n" +
                "        \"en\": \"Bidding Document Not Allowed to Order\",\n" +
                "        \"ru\": \"Документ для тендера не разрешает размещение заказа\",\n" +
                "        \"kk\": \"Тендерге арналған құжат тапсырысты орналастыруға рұқсат бермейді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"product.mismatch.bid.doc\",\n" +
                "        \"en\": \"Product Does Not Match Bidding Document\",\n" +
                "        \"ru\": \"Продукт не соответствует тендерному документу\",\n" +
                "        \"kk\": \"Өнім тендерлік құжатқа сәйкес келмейді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"quantity.mismatch.bid.doc\",\n" +
                "        \"en\": \"Order Quantity Does Not Match Bidding Document\",\n" +
                "        \"ru\": \"Количество заказанных товаров не соответствует тендерному документу\",\n" +
                "        \"kk\": \"Тапсырыс берілген тауарлардың саны тендерлік құжатқа сәйкес келмейді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"duplicate.order.on.bid.doc\",\n" +
                "        \"en\": \"Duplicate Order on Bidding Document\",\n" +
                "        \"ru\": \"Дублирование заказа в тендерном документе\",\n" +
                "        \"kk\": \"Тендерлік құжаттағы тапсырыстың қайталануы\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"please.try.again.later\",\n" +
                "        \"en\": \"Please Try Again Later\",\n" +
                "        \"ru\": \"Попробуйте позже\",\n" +
                "        \"kk\": \"Кейінірек көріңіз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"merging.orders.not.supported\",\n" +
                "        \"en\": \"Merging Orders Not Supported\",\n" +
                "        \"ru\": \"Объединение заказов не поддерживается\",\n" +
                "        \"kk\": \"Тапсырыстарды біріктіруге қолдау көрсетілмейді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"missing.split.billing.info\",\n" +
                "        \"en\": \"Missing Split Billing Information\",\n" +
                "        \"ru\": \"Отсутствует информация о разделении счета\",\n" +
                "        \"kk\": \"Шотты бөлу туралы ақпарат жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"gateway.error.split.payment\",\n" +
                "        \"en\": \"Gateway Error During Split Payment\",\n" +
                "        \"ru\": \"Ошибка шлюза при раздельной оплате\",\n" +
                "        \"kk\": \"Бөлек төлем кезіндегі шлюз қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"error.saving.split.record\",\n" +
                "        \"en\": \"Error Saving Split Billing Record\",\n" +
                "        \"ru\": \"Ошибка сохранения записи о раздельной оплате\",\n" +
                "        \"kk\": \"Бөлек төлем жазбасын сақтау қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"split.billing.trans.id.empty\",\n" +
                "        \"en\": \"Split Billing Payment Transaction ID Empty\",\n" +
                "        \"ru\": \"Пустой идентификатор транзакции раздельной оплаты\",\n" +
                "        \"kk\": \"Бос жеке төлем транзакциясының идентификаторы\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.number\",\n" +
                "        \"en\": \"Order Number\",\n" +
                "        \"ru\": \"Номер заказа\",\n" +
                "        \"kk\": \"Тапсырыс нөмірі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.receipt.fail\",\n" +
                "        \"en\": \"Order Receipt Confirmation Failed\",\n" +
                "        \"ru\": \"Ошибка подтверждения получения заказа\",\n" +
                "        \"kk\": \"Тапсырысты алуды растау қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.failed\",\n" +
                "        \"en\": \"Refund Failed\",\n" +
                "        \"ru\": \"Не удалось вернуть средства\",\n" +
                "        \"kk\": \"Қаражатты қайтару мүмкін болмады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"reason\",\n" +
                "        \"en\": \"Reason\",\n" +
                "        \"ru\": \"Причина\",\n" +
                "        \"kk\": \"Себебі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"non.shipped.order\",\n" +
                "        \"en\": \"Non-shipped Orders\",\n" +
                "        \"ru\": \"Заказы, не отправленные\",\n" +
                "        \"kk\": \"Жіберілмеген тапсырыстар\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.sync.limit.exceeded\",\n" +
                "        \"en\": \"Order Sync Limit Exceeded\",\n" +
                "        \"ru\": \"Превышен лимит синхронизации заказа\",\n" +
                "        \"kk\": \"Тапсырысты синхрондау лимитінен асып кетті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"retry\",\n" +
                "        \"en\": \"Retry\",\n" +
                "        \"ru\": \"Попробуйте снова\",\n" +
                "        \"kk\": \"Қайталап көріңіз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.delivery.notify.fail\",\n" +
                "        \"en\": \"Order Delivery Notification Failed\",\n" +
                "        \"ru\": \"Ошибка уведомления о доставке заказа\",\n" +
                "        \"kk\": \"Тапсырысты жеткізу туралы хабарландыру қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.query.fail\",\n" +
                "        \"en\": \"Order Query Failed\",\n" +
                "        \"ru\": \"Не удалось выполнить запрос на заказ\",\n" +
                "        \"kk\": \"Тапсырыс сұрауын орындау мүмкін болмады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.query.empty\",\n" +
                "        \"en\": \"Order Query Result Empty\",\n" +
                "        \"ru\": \"Результат запроса заказа пуст\",\n" +
                "        \"kk\": \"Тапсырыс сұрауының нәтижесі бос\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.failed.actual.shipped\",\n" +
                "        \"en\": \"Refund Failed, Order Actually Shipped\",\n" +
                "        \"ru\": \"Не удалось вернуть средства, заказ фактически был отправлен\",\n" +
                "        \"kk\": \"Қаражатты қайтару мүмкін болмады, тапсырыс іс жүзінде жіберілді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.push.fail\",\n" +
                "        \"en\": \"Order Push Failed\",\n" +
                "        \"ru\": \"Ошибка при передаче заказа\",\n" +
                "        \"kk\": \"Тапсырысты беру кезіндегі қате\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"sub.order.empty\",\n" +
                "        \"en\": \"Sub-order is Empty\",\n" +
                "        \"ru\": \"Подзаказ пуст\",\n" +
                "        \"kk\": \"Қосымша тапсырыс бос\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"after.sale\",\n" +
                "        \"en\": \"After Sale\",\n" +
                "        \"ru\": \"После продажи\",\n" +
                "        \"kk\": \"Сатудан кейін\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"retry.later\",\n" +
                "        \"en\": \"Retry Later\",\n" +
                "        \"ru\": \"Попробуйте позже\",\n" +
                "        \"kk\": \"Кейінірек көріңіз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"pushing\",\n" +
                "        \"en\": \"In Process of Pushing\",\n" +
                "        \"ru\": \"В процессе передачи\",\n" +
                "        \"kk\": \"Беру процесінде\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.direct.sale\",\n" +
                "        \"en\": \"No Direct Sale\",\n" +
                "        \"ru\": \"Нет прямых продаж\",\n" +
                "        \"kk\": \"Тікелей сату жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"consignment.order\",\n" +
                "        \"en\": \"Consignment Order\",\n" +
                "        \"ru\": \"Заказ по консигнации\",\n" +
                "        \"kk\": \"Консигнация бойынша тапсырыс\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.upload.needed\",\n" +
                "        \"en\": \"No Upload Needed\",\n" +
                "        \"ru\": \"Загрузка не требуется\",\n" +
                "        \"kk\": \"Жүктеу қажет емес\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"multiple.delivery.notifications\",\n" +
                "        \"en\": \"Multiple Delivery Notifications\",\n" +
                "        \"ru\": \"Несколько уведомлений о доставке\",\n" +
                "        \"kk\": \"Жеткізу туралы бірнеше хабарлама\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"vat.invoice.issued\",\n" +
                "        \"en\": \"VAT Invoice Issued\",\n" +
                "        \"ru\": \"Инвойс с учетом НДС выписан\",\n" +
                "        \"kk\": \"ҚҚС есебімен инвойс жазылды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"void.before.refund\",\n" +
                "        \"en\": \"Void Before Approving Refund\",\n" +
                "        \"ru\": \"Аннулировать до одобрения возврата\",\n" +
                "        \"kk\": \"Қайтару мақұлданғанға дейін күшін жою\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.and.goods\",\n" +
                "        \"en\": \"Vouchers and Goods Cannot be Ordered Together\",\n" +
                "        \"ru\": \"Купоны и товары не могут быть заказаны вместе\",\n" +
                "        \"kk\": \"Купондар мен тауарларға бірге тапсырыс беруге болмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.order.limit\",\n" +
                "        \"en\": \"Voucher Order Limit Exceeded\",\n" +
                "        \"ru\": \"Превышен лимит заказа ваучеров\",\n" +
                "        \"kk\": \"Ваучерлерге тапсырыс беру лимитінен асып кетті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"missing.voucher.info\",\n" +
                "        \"en\": \"Missing Voucher Information\",\n" +
                "        \"ru\": \"Отсутствует информация о ваучере\",\n" +
                "        \"kk\": \"Ваучер туралы ақпарат жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.voucher.template\",\n" +
                "        \"en\": \"No Voucher Template\",\n" +
                "        \"ru\": \"Нет шаблона ваучера\",\n" +
                "        \"kk\": \"Ваучер үлгісі жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.template.error\",\n" +
                "        \"en\": \"Voucher Template Error\",\n" +
                "        \"ru\": \"Ошибка шаблона ваучера\",\n" +
                "        \"kk\": \"Ваучер үлгісінің қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"duplicate.voucher.issue\",\n" +
                "        \"en\": \"Duplicate Voucher Issued\",\n" +
                "        \"ru\": \"Дублированный ваучер\",\n" +
                "        \"kk\": \"Қайталанатын ваучер\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.not.exist\",\n" +
                "        \"en\": \"Voucher Does Not Exist\",\n" +
                "        \"ru\": \"Ваучер не существует\",\n" +
                "        \"kk\": \"Ваучер жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invalid.voucher.status\",\n" +
                "        \"en\": \"Invalid Voucher Status\",\n" +
                "        \"ru\": \"Неверный статус ваучера\",\n" +
                "        \"kk\": \"Ваучер мәртебесі қате\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.redeemed\",\n" +
                "        \"en\": \"Voucher Redeemed\",\n" +
                "        \"ru\": \"Ваучер выкуплен\",\n" +
                "        \"kk\": \"Ваучер сатып алынды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.repeated.redemption\",\n" +
                "        \"en\": \"No Repeated Redemption\",\n" +
                "        \"ru\": \"Нет повторных выкупов\",\n" +
                "        \"kk\": \"Қайталап сатып алулар жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.not.in.validity\",\n" +
                "        \"en\": \"Voucher Not in Validity Period\",\n" +
                "        \"ru\": \"Ваучер не в периоде действия\",\n" +
                "        \"kk\": \"Ваучер әрекет ету кезеңінде емес\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"full.voucher.refund\",\n" +
                "        \"en\": \"Full Voucher Refund\",\n" +
                "        \"ru\": \"Полный возврат ваучера\",\n" +
                "        \"kk\": \"Ваучерді толық қайтару\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"return.quantity.all\",\n" +
                "        \"en\": \"Return Quantity Must Be All\",\n" +
                "        \"ru\": \"Количество возврата должно быть полным\",\n" +
                "        \"kk\": \"Қайтару саны толық болуы керек\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"final.payment.time.not.yet\",\n" +
                "        \"en\": \"Final Payment Time Not Yet Reached\",\n" +
                "        \"ru\": \"Время окончательной оплаты еще не наступило\",\n" +
                "        \"kk\": \"Соңғы төлем уақыты әлі келген жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"final.payment.time.ended\",\n" +
                "        \"en\": \"Final Payment Time Ended\",\n" +
                "        \"ru\": \"Время окончательной оплаты истекло\",\n" +
                "        \"kk\": \"Соңғы төлем уақыты аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"presale.setting.error\",\n" +
                "        \"en\": \"Presale Activity Setting Error\",\n" +
                "        \"ru\": \"Ошибка настройки предварительной продажи\",\n" +
                "        \"kk\": \"Алдын ала сатуды реттеу қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"deposit.product.setting.error\",\n" +
                "        \"en\": \"Deposit Product Setting Incorrect\",\n" +
                "        \"ru\": \"Неверная настройка товара с депозитом\",\n" +
                "        \"kk\": \"Депозиті бар тауарды қате реттеу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"delivery.date.too.early\",\n" +
                "        \"en\": \"Delivery Date Set Too Early\",\n" +
                "        \"ru\": \"Установлена слишком ранняя дата доставки\",\n" +
                "        \"kk\": \"Жеткізу күні тым ерте белгіленді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.not.found\",\n" +
                "        \"en\": \"Main Order Information Not Found\",\n" +
                "        \"ru\": \"Информация о главном заказе не найдена\",\n" +
                "        \"kk\": \"Негізгі тапсырыс туралы ақпарат табылған жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.status.not.confirmed\",\n" +
                "        \"en\": \"Order Status Not Confirmed\",\n" +
                "        \"ru\": \"Статус заказа не подтвержден\",\n" +
                "        \"kk\": \"Тапсырыс мәртебесі расталмаған\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.requested\",\n" +
                "        \"en\": \"Invoice Requested\",\n" +
                "        \"ru\": \"Инвойс запрошен\",\n" +
                "        \"kk\": \"Инвойс сұралды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.duplicate.invoice.request\",\n" +
                "        \"en\": \"Do Not Request Invoice Again\",\n" +
                "        \"ru\": \"Не запрашивайте инвойс повторно\",\n" +
                "        \"kk\": \"Инвойсты қайта сұрамаңыз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.head.not.found\",\n" +
                "        \"en\": \"Invoice Title Not Found\",\n" +
                "        \"ru\": \"Название инвойса не найдено\",\n" +
                "        \"kk\": \"Инвойс атауы табылмады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invalid.invoice.type\",\n" +
                "        \"en\": \"Invalid Invoice Type\",\n" +
                "        \"ru\": \"Неверный тип инвойса\",\n" +
                "        \"kk\": \"Инвойстың түрі қате\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.record.not.found\",\n" +
                "        \"en\": \"Current Invoice Record Not Found\",\n" +
                "        \"ru\": \"Текущий учет инвойса не найден\",\n" +
                "        \"kk\": \"Ағымдағы инвойстың есебі табылмады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.voiding\",\n" +
                "        \"en\": \"Current Invoice Being Voided\",\n" +
                "        \"ru\": \"Текущий инвойс аннулируется\",\n" +
                "        \"kk\": \"Ағымдағы инвойс жойылады\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.duplicate.submission\",\n" +
                "        \"en\": \"No Duplicate Submissions\",\n" +
                "        \"ru\": \"Нет дублирующих заявок\",\n" +
                "        \"kk\": \"Қайталанатын өтінімдер жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.voided\",\n" +
                "        \"en\": \"Current Invoice Voided\",\n" +
                "        \"ru\": \"Текущий инвойс аннулирован\",\n" +
                "        \"kk\": \"Ағымдағы инвойс жойылды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"operation.not.allowed\",\n" +
                "        \"en\": \"Operation Not Allowed\",\n" +
                "        \"ru\": \"Операция не разрешена\",\n" +
                "        \"kk\": \"Операцияға рұқсат етілмейді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"ticket.center.request.fail\",\n" +
                "        \"en\": \"Ticket Center Request Failed\",\n" +
                "        \"ru\": \"Ошибка запроса в Центре билетов\",\n" +
                "        \"kk\": \"Билеттер орталығындағы сұрау қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"try.later\",\n" +
                "        \"en\": \"Please Try Again Later\",\n" +
                "        \"ru\": \"Попробуйте снова позже\",\n" +
                "        \"kk\": \"Кейінірек қайталап көріңіз\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"all.after.sales.complete\",\n" +
                "        \"en\": \"All After Sales Services Completed\",\n" +
                "        \"ru\": \"Все услуги после продажи завершены\",\n" +
                "        \"kk\": \"Сатудан кейінгі барлық қызметтер аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.invoice.for.promo\",\n" +
                "        \"en\": \"No Invoice for Promotional Items\",\n" +
                "        \"ru\": \"Нет инвойса для рекламных товаров\",\n" +
                "        \"kk\": \"Жарнамалық тауарлар үшін инвойс жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.entry.closed\",\n" +
                "        \"en\": \"Invoice Entry Closed\",\n" +
                "        \"ru\": \"Ввод инвойса закрыт\",\n" +
                "        \"kk\": \"Инвойсты енгізу жабық\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.invoice\",\n" +
                "        \"en\": \"No Invoice\",\n" +
                "        \"ru\": \"Нет инвойса\",\n" +
                "        \"kk\": \"Инвойс жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"profit.account\",\n" +
                "        \"en\": \"Profit Account\",\n" +
                "        \"ru\": \"Счет прибылей\",\n" +
                "        \"kk\": \"Пайда шоты\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"merchant.account\",\n" +
                "        \"en\": \"Merchant Account\",\n" +
                "        \"ru\": \"Счет продавца\",\n" +
                "        \"kk\": \"Сатушының шоты\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"initializing\",\n" +
                "        \"en\": \"Initializing\",\n" +
                "        \"ru\": \"Инициализация\",\n" +
                "        \"kk\": \"Инициализация\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"processing\",\n" +
                "        \"en\": \"Processing\",\n" +
                "        \"ru\": \"Обработка\",\n" +
                "        \"kk\": \"Өңдеу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"success\",\n" +
                "        \"en\": \"Success\",\n" +
                "        \"ru\": \"Успешно\",\n" +
                "        \"kk\": \"Сәтті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"failure\",\n" +
                "        \"en\": \"Failure\",\n" +
                "        \"ru\": \"Не удалось\",\n" +
                "        \"kk\": \"Сәтсіз аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"payment.status.mismatch\",\n" +
                "        \"en\": \"Payment Status Mismatch\",\n" +
                "        \"ru\": \"Несоответствие статуса платежа\",\n" +
                "        \"kk\": \"Төлем мәртебесінің сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"payment.amount.mismatch\",\n" +
                "        \"en\": \"Payment Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы платежа\",\n" +
                "        \"kk\": \"Төлем сомасының сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"split.amount.mismatch\",\n" +
                "        \"en\": \"Split Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы разделения\",\n" +
                "        \"kk\": \"Бөлу сомасының сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.amount.mismatch\",\n" +
                "        \"en\": \"Refund Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы возврата\",\n" +
                "        \"kk\": \"Қайтару сомасының сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.status.mismatch\",\n" +
                "        \"en\": \"Order Status Mismatch\",\n" +
                "        \"ru\": \"Несоответствие статуса заказа\",\n" +
                "        \"kk\": \"Тапсырыс мәртебесінің сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"after.sale.status.mismatch\",\n" +
                "        \"en\": \"After Sale Status Mismatch\",\n" +
                "        \"ru\": \"Несоответствие статуса послепродажного обслуживания\",\n" +
                "        \"kk\": \"Сатудан кейінгі қызмет көрсету мәртебесінің сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.amount.error\",\n" +
                "        \"en\": \"Main Order Amount Error\",\n" +
                "        \"ru\": \"Ошибка суммы основного заказа\",\n" +
                "        \"kk\": \"Негізгі тапсырыс сомасының қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"sub.order.split.error\",\n" +
                "        \"en\": \"Sub-order Split Error\",\n" +
                "        \"ru\": \"Ошибка разделения подзаказа\",\n" +
                "        \"kk\": \"Қосалқы тапсырысты бөлу қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"payment.amount.error\",\n" +
                "        \"en\": \"Payment Amount Error\",\n" +
                "        \"ru\": \"Ошибка суммы платежа\",\n" +
                "        \"kk\": \"Төлем сомасының қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.payment.mismatch\",\n" +
                "        \"en\": \"Main Order Payment Mismatch\",\n" +
                "        \"ru\": \"Несоответствие основного заказа и платежа\",\n" +
                "        \"kk\": \"Негізгі тапсырыс пен төлемнің сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"payment.and.transaction.mismatch\",\n" +
                "        \"en\": \"Payment and Transaction Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы платежа и транзакции\",\n" +
                "        \"kk\": \"Төлем сомасы мен транзакцияның сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"after.sale.amount.error\",\n" +
                "        \"en\": \"After Sale Amount Error\",\n" +
                "        \"ru\": \"Ошибка суммы послепродажного обслуживания\",\n" +
                "        \"kk\": \"Сатудан кейінгі қызмет көрсету сомасының қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.amount.error\",\n" +
                "        \"en\": \"Refund Amount Error\",\n" +
                "        \"ru\": \"Ошибка суммы возврата\",\n" +
                "        \"kk\": \"Қайтару сомасының қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"after.sale.refund.mismatch\",\n" +
                "        \"en\": \"After Sale and Refund Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы послепродажного обслуживания и возврата\",\n" +
                "        \"kk\": \"Сатудан кейінгі қызмет көрсету және қайтару сомасының сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.detail.mismatch\",\n" +
                "        \"en\": \"Refund and Refund Detail Amount Mismatch\",\n" +
                "        \"ru\": \"Несоответствие суммы возврата и детальной суммы возврата\",\n" +
                "        \"kk\": \"Қайтару сомасы мен қайтарудың егжей-тегжейлі сомасының сәйкес келмеуі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"points.deduction.error\",\n" +
                "        \"en\": \"Points Deduction Error\",\n" +
                "        \"ru\": \"Ошибка списания баллов\",\n" +
                "        \"kk\": \"Ұпайларды есептен шығару қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"after.sale.points.error\",\n" +
                "        \"en\": \"After Sale Points Deduction Error\",\n" +
                "        \"ru\": \"Ошибка списания баллов после продажи\",\n" +
                "        \"kk\": \"Сатудан кейінгі ұпайларды есептен шығару қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"direct.sales\",\n" +
                "        \"en\": \"Direct Sales\",\n" +
                "        \"ru\": \"Прямые продажи\",\n" +
                "        \"kk\": \"Тікелей сату\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"consignment\",\n" +
                "        \"en\": \"Consignment\",\n" +
                "        \"ru\": \"Консигнация\",\n" +
                "        \"kk\": \"Консигнация\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"order.payment.split\",\n" +
                "        \"en\": \"Order Payment Split\",\n" +
                "        \"ru\": \"Разделение платежа заказа\",\n" +
                "        \"kk\": \"Тапсырыс төлемін бөлу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.payment.split\",\n" +
                "        \"en\": \"Refund Payment Split\",\n" +
                "        \"ru\": \"Разделение платежа возврата\",\n" +
                "        \"kk\": \"Төлемді қайтаруды бөлу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"review.points.award\",\n" +
                "        \"en\": \"Review Points Award\",\n" +
                "        \"ru\": \"Признание баллов за отзыв\",\n" +
                "        \"kk\": \"Қайтарып алу үшін ұпайларды тану\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"gift.info.error\",\n" +
                "        \"en\": \"Gift Information Error\",\n" +
                "        \"ru\": \"Ошибка информации о подарке\",\n" +
                "        \"kk\": \"Сыйлық туралы ақпарат қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"items\",\n" +
                "        \"en\": \"Items\",\n" +
                "        \"ru\": \"Товары\",\n" +
                "        \"kk\": \"Тауарлар\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.points\",\n" +
                "        \"en\": \"Refund Should Return Points\",\n" +
                "        \"ru\": \"Возврат баллов должен быть выполнен\",\n" +
                "        \"kk\": \"Ұпайларды қайтару орындалуы керек\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"points.deducted\",\n" +
                "        \"en\": \"Points Deducted Due to Insufficient Balance\",\n" +
                "        \"ru\": \"Списанные баллы из-за недостаточности баланса\",\n" +
                "        \"kk\": \"Баланстың жеткіліксіздігіне байланысты есептен шығарылған ұпайлар\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"missing.voucher\",\n" +
                "        \"en\": \"Missing Voucher\",\n" +
                "        \"ru\": \"Отсутствует ваучер\",\n" +
                "        \"kk\": \"Ваучер жоқ\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"info.error\",\n" +
                "        \"en\": \"Information Error\",\n" +
                "        \"ru\": \"Ошибка информации\",\n" +
                "        \"kk\": \"Ақпарат қатесі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"request.success\",\n" +
                "        \"en\": \"Request Successful\",\n" +
                "        \"ru\": \"Запрос успешен\",\n" +
                "        \"kk\": \"Сұрау сәтті\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"bid.document.query\",\n" +
                "        \"en\": \"Query Bidding Document\",\n" +
                "        \"ru\": \"Запрос тендерного документа\",\n" +
                "        \"kk\": \"Тендерлік құжатқа сұрау салу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"quote.document.query\",\n" +
                "        \"en\": \"Query Quotation Document\",\n" +
                "        \"ru\": \"Запрос документа предложения\",\n" +
                "        \"kk\": \"Ұсыныс құжатын сұрау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"update.quote.document\",\n" +
                "        \"en\": \"Update Quotation Document\",\n" +
                "        \"ru\": \"Обновить документ предложения\",\n" +
                "        \"kk\": \"Ұсыныс құжатын жаңарту\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"update.bid.document\",\n" +
                "        \"en\": \"Update Bidding Document\",\n" +
                "        \"ru\": \"Обновить тендерный документ\",\n" +
                "        \"kk\": \"Тендерлік құжатты жаңарту\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"query.points.config\",\n" +
                "        \"en\": \"Query Points Award Configuration\",\n" +
                "        \"ru\": \"Запрос конфигурации награды баллов\",\n" +
                "        \"kk\": \"Ұпайлар сыйақысының конфигурациясын сұрау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"points.award\",\n" +
                "        \"en\": \"Points Award\",\n" +
                "        \"ru\": \"Награда баллов\",\n" +
                "        \"kk\": \"Ұпайлар марапаты\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"return.awarded.points\",\n" +
                "        \"en\": \"Return Awarded Points\",\n" +
                "        \"ru\": \"Возврат награжденных баллов\",\n" +
                "        \"kk\": \"Марапатталған ұпайларды қайтару\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"query.points.product\",\n" +
                "        \"en\": \"Query Points Product\",\n" +
                "        \"ru\": \"Запрос продукта для баллов\",\n" +
                "        \"kk\": \"Ұпайлар үшін өнімді сұрау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"send.message\",\n" +
                "        \"en\": \"Send Message\",\n" +
                "        \"ru\": \"Отправить сообщение\",\n" +
                "        \"kk\": \"Хабарлама жіберу\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invalid.bid.document\",\n" +
                "        \"en\": \"Invalid Bidding Document\",\n" +
                "        \"ru\": \"Неверный тендерный документ\",\n" +
                "        \"kk\": \"Қате тендерлік құжат\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"name.cannot.be.empty\",\n" +
                "        \"en\": \"Name Cannot Be Empty\",\n" +
                "        \"ru\": \"Имя не может быть пустым\",\n" +
                "        \"kk\": \"Аты бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"online\",\n" +
                "        \"en\": \"Online\",\n" +
                "        \"ru\": \"Онлайн\",\n" +
                "        \"kk\": \"Онлайн\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"offline\",\n" +
                "        \"en\": \"Offline\",\n" +
                "        \"ru\": \"Офлайн\",\n" +
                "        \"kk\": \"Офлайн\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"import.export\",\n" +
                "        \"en\": \"Import Export\",\n" +
                "        \"ru\": \"Импорт/Экспорт\",\n" +
                "        \"kk\": \"Импорт/Экспорт\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.cannot.be\",\n" +
                "        \"en\": \"Main Order Cannot Be\",\n" +
                "        \"ru\": \"Основной заказ не может быть\",\n" +
                "        \"kk\": \"Негізгі тапсырыс бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.type.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Type Cannot Be Empty\",\n" +
                "        \"ru\": \"Тип инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс түрі бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.head.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Head Cannot Be Empty\",\n" +
                "        \"ru\": \"Заголовок инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс тақырыбы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"buyer.cannot.be.empty\",\n" +
                "        \"en\": \"Buyer Cannot Be Empty\",\n" +
                "        \"ru\": \"Покупатель не может быть пустым\",\n" +
                "        \"kk\": \"Сатып алушы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"special.invoice.info.cannot.be.empty\",\n" +
                "        \"en\": \"Special Invoice Information Cannot Be Empty\",\n" +
                "        \"ru\": \"Специальная информация об инвойсе не может быть пустой\",\n" +
                "        \"kk\": \"Инвойс туралы арнайы ақпарат бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"seller.cannot.be.empty\",\n" +
                "        \"en\": \"Seller Cannot Be Empty\",\n" +
                "        \"ru\": \"Продавец не может быть пустым\",\n" +
                "        \"kk\": \"Сатушы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.type.not.normal\",\n" +
                "        \"en\": \"Invoice Type Not Normal Invoice\",\n" +
                "        \"ru\": \"Тип инвойса: не является обычным инвойсом\",\n" +
                "        \"kk\": \"Инвойс түрі: жалпы инвойс болып табылмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.cannot.be.empty\",\n" +
                "        \"en\": \"Main Order Cannot Be Empty\",\n" +
                "        \"ru\": \"Основной заказ не может быть пустым\",\n" +
                "        \"kk\": \"Негізгі тапсырыс бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.code.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Code Cannot Be Empty\",\n" +
                "        \"ru\": \"Код инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс коды бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.number.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Number Cannot Be Empty\",\n" +
                "        \"ru\": \"Номер инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс нөмірі бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.status.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Status Cannot Be Empty\",\n" +
                "        \"ru\": \"Статус инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс мәртебесі бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.query.total.cannot.be.empty\",\n" +
                "        \"en\": \"Invoice Query Total Cannot Be Empty\",\n" +
                "        \"ru\": \"Итоговая сумма запроса инвойса не может быть пустой\",\n" +
                "        \"kk\": \"Инвойс сұрауының қорытынды сомасы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.query\",\n" +
                "        \"en\": \"Invoice Query\",\n" +
                "        \"ru\": \"Запрос инвойса\",\n" +
                "        \"kk\": \"Инвойс сұрауы\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.query.empty\",\n" +
                "        \"en\": \"Invoice Query and Order Cannot Both Be Empty\",\n" +
                "        \"ru\": \"Запрос ивойса и заказ не могут быть пустыми одновременно\",\n" +
                "        \"kk\": \"Ивойс сұрауы мен тапсырысы бір уақытта бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.code.cannot.be.empty\",\n" +
                "        \"en\": \"Voucher Code Cannot Be Empty\",\n" +
                "        \"ru\": \"Код ваучера не может быть пустым\",\n" +
                "        \"kk\": \"Ваучер коды бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"query.conditions.cannot.be.empty\",\n" +
                "        \"en\": \"Query Conditions Cannot All Be Empty\",\n" +
                "        \"ru\": \"Условия запроса не могут быть все пустыми\",\n" +
                "        \"kk\": \"Сұраныс шарттары түгелдей бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"revoke.invoice\",\n" +
                "        \"en\": \"Revoke Invoice\",\n" +
                "        \"ru\": \"Отменить инвойс\",\n" +
                "        \"kk\": \"Инвойсты жою\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"revoke.invoice.and.order.cannot.be.empty\",\n" +
                "        \"en\": \"Revoke Invoice and Order Cannot Both Be Empty\",\n" +
                "        \"ru\": \"Отмена инвойса и заказа не могут быть пустыми одновременно\",\n" +
                "        \"kk\": \"Инвойс пен тапсырыстың күшін жою бір уақытта бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.request\",\n" +
                "        \"en\": \"Refund Request\",\n" +
                "        \"ru\": \"Запрос возврата\",\n" +
                "        \"kk\": \"Қайтару туралы сұрау\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.agree\",\n" +
                "        \"en\": \"Refund Agree\",\n" +
                "        \"ru\": \"Согласие на возврат\",\n" +
                "        \"kk\": \"Қайтаруға келісім\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.reject\",\n" +
                "        \"en\": \"Refund Reject\",\n" +
                "        \"ru\": \"Отклонить возврат\",\n" +
                "        \"kk\": \"Қайтарудан бас тарту\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"normal.redo.invoice\",\n" +
                "        \"en\": \"Normal Redo Invoice\",\n" +
                "        \"ru\": \"Нормальное повторное создание инвойса\",\n" +
                "        \"kk\": \"Инвойсты қалыпты қайта құру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.redo.invoice\",\n" +
                "        \"en\": \"Redo Invoice Due to Refund\",\n" +
                "        \"ru\": \"Пересоздание инвойса из-за возврата\",\n" +
                "        \"kk\": \"Қайтаруға байланысты инвойсты қайта құру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"normal.void.invoice\",\n" +
                "        \"en\": \"Normal Void Invoice\",\n" +
                "        \"ru\": \"Нормальное аннлиурование инвойса\",\n" +
                "        \"kk\": \"Қалыпты инвойсты жою\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"refund.void.invoice\",\n" +
                "        \"en\": \"Void Invoice Due to Refund\",\n" +
                "        \"ru\": \"Аннулирование инвойса из-за возврата\",\n" +
                "        \"kk\": \"Қайтаруға байланысты инвойсты жою\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"redo.invoice\",\n" +
                "        \"en\": \"Redo Invoice\",\n" +
                "        \"ru\": \"Пересоздание инвойса\",\n" +
                "        \"kk\": \"Инвойсты қайта құру\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"void.invoice\",\n" +
                "        \"en\": \"Void Invoice\",\n" +
                "        \"ru\": \"Аннулировать инвойс\",\n" +
                "        \"kk\": \"Инвойсты жою\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"application.failed\",\n" +
                "        \"en\": \"Application Failed\",\n" +
                "        \"ru\": \"Заявка не удалась\",\n" +
                "        \"kk\": \"Өтініш сәтсіз аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"not.invoiced\",\n" +
                "        \"en\": \"Not Invoiced\",\n" +
                "        \"ru\": \"Инвойс не выставлен\",\n" +
                "        \"kk\": \"Инвойс қойылмаған\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoiced\",\n" +
                "        \"en\": \"Invoiced\",\n" +
                "        \"ru\": \"Инвойс выставлен\",\n" +
                "        \"kk\": \"Инвойс қойылды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"partial.invoiced\",\n" +
                "        \"en\": \"Partially Invoiced\",\n" +
                "        \"ru\": \"Инвойс выставлен частично\",\n" +
                "        \"kk\": \"Инвойс ішінара қойылды \"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"revoke.completed\",\n" +
                "        \"en\": \"Revoke Completed\",\n" +
                "        \"ru\": \"Отмена завершена\",\n" +
                "        \"kk\": \"Жою аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"void.completed\",\n" +
                "        \"en\": \"Void Completed\",\n" +
                "        \"ru\": \"Аннулирование завершено\",\n" +
                "        \"kk\": \"Күшін жою аяқталды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"direct.sales.mode\",\n" +
                "        \"en\": \"Direct Sales Mode\",\n" +
                "        \"ru\": \"Режим прямых продаж\",\n" +
                "        \"kk\": \"Тікелей сату режимі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"consignment.mode\",\n" +
                "        \"en\": \"Consignment Mode\",\n" +
                "        \"ru\": \"Режим консигнации\",\n" +
                "        \"kk\": \"Консигнация режимі\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"e.invoice\",\n" +
                "        \"en\": \"Electronic Invoice\",\n" +
                "        \"ru\": \"Электронный инвойс\",\n" +
                "        \"kk\": \"Электрондық инвойс\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"vat.invoice\",\n" +
                "        \"en\": \"VAT Invoice\",\n" +
                "        \"ru\": \"Инвойс с учетом НДС\",\n" +
                "        \"kk\": \"ҚҚС ескерілген инвойс\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"overdue.not.paid\",\n" +
                "        \"en\": \"Overdue Not Paid\",\n" +
                "        \"ru\": \"Срок оплаты истек, не оплачено\",\n" +
                "        \"kk\": \"Төлем мерзімі аяқталды, төленбеді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"agreement.cancelled\",\n" +
                "        \"en\": \"Agreement Cancelled\",\n" +
                "        \"ru\": \"Соглашение отменено\",\n" +
                "        \"kk\": \"Келісім жойылды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"voucher.sent\",\n" +
                "        \"en\": \"Voucher Sent\",\n" +
                "        \"ru\": \"Ваучер отправлен\",\n" +
                "        \"kk\": \"Ваучер жіберілді\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"no.refund.required\",\n" +
                "        \"en\": \"No Refund Required\",\n" +
                "        \"ru\": \"Возврат не требуется\",\n" +
                "        \"kk\": \"Қайтару қажет емес\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"merchant.refunded\",\n" +
                "        \"en\": \"Merchant Refunded\",\n" +
                "        \"ru\": \"Торговец вернул деньги\",\n" +
                "        \"kk\": \"Саудагер ақшаны қайтарды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"buyer.seller.not.both.null\",\n" +
                "        \"en\": \"Buyer and seller cannot both be null\",\n" +
                "        \"ru\": \"Покупатель и продавец не могут быть оба пустыми\",\n" +
                "        \"kk\": \"Сатып алушы мен сатушы екеуі де бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.id.not.null\",\n" +
                "        \"en\": \"Main order ID cannot be null\",\n" +
                "        \"ru\": \"Идентификатор основного заказа не может быть пустым\",\n" +
                "        \"kk\": \"Негізгі тапсырыс идентификаторы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"main.order.id.required\",\n" +
                "        \"en\": \"Main order ID cannot be empty\",\n" +
                "        \"ru\": \"Идентификатор основного заказа не может быть пустым\",\n" +
                "        \"kk\": \"Негізгі тапсырыс идентификаторы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"buyer.id.required\",\n" +
                "        \"en\": \"Buyer ID cannot be empty\",\n" +
                "        \"ru\": \"Идентификатор покупателя не может быть пустым\",\n" +
                "        \"kk\": \"Сатып алушының идентификаторы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"seller.id.required\",\n" +
                "        \"en\": \"Seller ID cannot be empty\",\n" +
                "        \"ru\": \"Идентификатор продавца не может быть пустым\",\n" +
                "        \"kk\": \"Сатушы идентификаторы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.info.required\",\n" +
                "        \"en\": \"Invoice information cannot be empty\",\n" +
                "        \"ru\": \"Информация об инвойсе не может быть пустой\",\n" +
                "        \"kk\": \"Инвойс туралы ақпарат бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"invoice.title.id.required\",\n" +
                "        \"en\": \"Invoice title ID cannot be empty\",\n" +
                "        \"ru\": \"Идентификатор заголовка инвойса не может быть пустым\",\n" +
                "        \"kk\": \"Инвойс тақырыбының идентификаторы бос бола алмайды\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"key\": \"query.invoice.type.required\",\n" +
                "        \"en\": \"Invoice type for query cannot be empty\",\n" +
                "        \"ru\": \"Тип инвойса для запроса не может быть пустым\",\n" +
                "        \"kk\": \"Сұранысқа арналған инвойс түрі бос бола алмайды\"\n" +
                "    }\n" +
                "]";
        
        JSONArray jsonObj = JSON.parseArray(jsonArray);
        List<Map<String, String>> langList = new ArrayList<>();
        Map<String, String> en = new HashMap<>();
        en.put("lang", "en");
        en.put("path", "messages_mall_trade_center_en.properties");
        langList.add(en);

        Map<String, String> kk = new HashMap<>();
        kk.put("lang", "kk");
        kk.put("path", "messages_mall_trade_center_kk.properties");
        langList.add(kk);

        Map<String, String> ru = new HashMap<>();
        ru.put("lang", "ru");
        ru.put("path", "messages_mall_trade_center_ru.properties");
        langList.add(ru);

        // 获取resources目录下的example.properties文件的输入流
//        InputStream inputStream = PropertiesUtil2.class.getClassLoader().getResourceAsStream("messages_mall_trade_center.properties");
//        if (inputStream != null) {
//            // 加载输入流
//            defalutProp.load(inputStream);
//            // 关闭输入流
//            inputStream.close();
//        }
        // 所有的key

        for ( Map<String, String> lang : langList) {
            System.out.println("----------------------" + JSON.toJSONString(lang) + "-------------------------------");
            Properties langProp = new Properties();
            // 获取resources目录下的example.properties文件的输入流
            InputStream inputStream = PropertiesI18nUtil.class.getClassLoader().getResourceAsStream(lang.get("path"));
            if (inputStream != null) {
                // 加载输入流
                langProp.load(inputStream);
                // 关闭输入流
                inputStream.close();
            }
            List<String> un = new ArrayList<>();
            for (Object key : langProp.stringPropertyNames()) {
                boolean exist = Boolean.FALSE;
                for (Integer index = 0; index < jsonObj.size(); index++) {
                    JSONObject j = jsonObj.getJSONObject(index);
                    if (key.equals(j.getString("key"))) {
                        System.out.println(String.valueOf(key) + "=" + j.getString(lang.get("lang")));
                        exist = Boolean.TRUE;
                        break;
                    }
                }
                if (!exist) {
                    un.add(String.valueOf(key) + "=" + langProp.get(key) + "未提供");
                }
            }
            for (String key : un) {
                System.out.println(key);
            }
            System.out.println("----------------------" + lang + "-------------end------------------");
            System.out.println("");
        }
    }
}
