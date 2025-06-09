package com.aliyun.gts.gmall.manager.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;

/**
 * 多语言配置比较工具类，在项目中无用
 */
public class PropertiesI18nUtil {

    public static void main(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("[{\"key\":\"logistics.company.type.required\",\"en\":\"Logistics company type field is required\",\"ru\":\"Поле \\\"\\\"Тип логистической компании\\\"\\\" обязательно для заполнения\",\"kk\":\"Логистикалық компанияның түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Логистикалық компанияның түрі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"logistics.number.required\",\"en\":\"Logistics company number field is required\",\"ru\":\"Поле \\\"\\\"Номер логистической компании\\\"\\\" обязательно для заполнения\",\"kk\":\"Логистикалық компанияның нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Логистикалық компанияның нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"add.to.cart.qty.required\",\"en\":\"Quantity field for adding to the cart cannot be empty\",\"ru\":\"Поле \\\"\\\"Количество\\\"\\\" для добавления в корзину не может быть пустым\",\"kk\":\"Себетке қосуға арналған \\\"\\\"Саны\\\"\\\" өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"select.modify.content\",\"en\":\"Please select the content to modify\",\"ru\":\"Пожалуйста, выберите содержимое для изменения\",\"kk\":\"Өзгерту үшін құрамды таңдауды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"main.order.number.required\",\"en\":\"Main order number field is required\",\"ru\":\"Поле \\\"\\\"Номер основного заказа\\\"\\\" обязательно для заполнения\",\"kk\":\"Негізгі тапсырыс нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Негізгі тапсырыс нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"order.total.amount.required\",\"en\":\"Total order amount field is required\",\"ru\":\"Поле \\\"\\\"Общая сумма заказа\\\"\\\" обязательно для заполнения\",\"kk\":\"Тапсырыстың жалпы сомасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тапсырыстың жалпы сомасы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"actual.payment.required\",\"en\":\"Actual payment amount field is required\",\"ru\":\"Поле \\\"\\\"Фактическая сумма платежа обязательно для заполнения\",\"kk\":\"Нақты төлем сомасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Нақты төлем сомасы\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"points.deduction.amount.required\",\"en\":\"Amount of points to be deducted field is required\",\"ru\":\"Поле \\\"\\\"Сумма списываемых баллов обязательно для заполнения\",\"kk\":\"Есептен шығарылатын ұпайлардың сомасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Есептен шығарылатын ұпайлардың сомасы\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"points.deduction.qty.required\",\"en\":\"Quantity of points to be deducted field is required\",\"ru\":\"Поле \\\"\\\"Количество списываемых баллов\\\"\\\" обязательно для заполнения\",\"kk\":\"Есептен шығарылатын ұпайлар саны өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Есептен шығарылатын ұпайлар саны\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"order.total.amount.pos.int\",\"en\":\"Total order amount must be a positive integer\",\"ru\":\"Общая сумма заказа должна быть положительным целым числом\",\"kk\":\"Тапсырыстың жалпы сомасы оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"actual.payment.pos.int\",\"en\":\"Actual payment amount must be a positive integer\",\"ru\":\"Фактическая сумма оплаты должна быть положительным целым числом\",\"kk\":\"Нақты төлем сомасы оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.deduction.amount.pos.int\",\"en\":\"Amount of points to be deducted must be a positive integer\",\"ru\":\"Сумма списания баллов должна быть положительным целым числом\",\"kk\":\"Ұпайларды есептен шығару сомасы оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.deduction.qty.pos.int\",\"en\":\"Quantity of points to be deducted must be a positive integer\",\"ru\":\"Количество списания баллов должно быть положительным целым числом\",\"kk\":\"Ұпайларды есептен шығару саны оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.amount.exception\",\"en\":\"Payment Amount Exception\",\"ru\":\"Исключение в сумме оплаты\",\"kk\":\"Төлем сомасын алып тастау\",\"ru_new\":\"Исключение суммы платежа\",\"kk new\":\"Төлем сомасындағы ерекшелік\"}\n" +
                "{\"key\":\"payment.channel.required\",\"en\":\"Payment channel field cannot be empty\",\"ru\":\"Поле \\\"\\\"канал оплаты\\\"\\\" не может быть пустым\",\"kk\":\"Төлем арнасы өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Төлем арнасы\\\"\\\" өрісі бос бола алмайды\"}\n" +
                "{\"key\":\"merge.payment.main.order.mismatch\",\"en\":\"Mismatch of main order number when merging payments\",\"ru\":\"Несоответствие основного номера заказа при объединении платежей\",\"kk\":\"Төлемдерді біріктіру кезінде тапсырыстың негізгі нөмірінің сәйкес келмеуі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"main.order\",\"en\":\"Main Order\",\"ru\":\"Основной заказ\",\"kk\":\"Негізгі тапсырыс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.serial.number.required\",\"en\":\"Payment serial number field is required\",\"ru\":\"Поле \\\"\\\"Серийный номер платежа\\\"\\\" обязательно для заполнения\",\"kk\":\"Төлемнің сериялық нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Төлемнің сериялық нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"order.confirmation.info.required\",\"en\":\"Order confirmation information field is required\",\"ru\":\"Поле \\\"\\\"Информация о подтверждении заказа\\\"\\\" обязательно для заполнения\",\"kk\":\"Тапсырысты растау туралы ақпарат өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тапсырысты растау туралы ақпарат\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"seller.info.list.required\",\"en\":\"Seller information list field is required\",\"ru\":\"Поле \\\"\\\"Список информации о продавце\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатушы туралы ақпарат тізімі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатушы туралы ақпарат тізімі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"merchants\",\"en\":\"Merchants\",\"ru\":\"Продавцы\",\"kk\":\"Сатушылар\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"after.sales.service.type.required\",\"en\":\"After-sales service type field is required\",\"ru\":\"Поле \\\"\\\"Тип послепродажного обслуживания\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатудан кейінгі қызмет түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатудан кейінгі қызмет түрі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n");
        sb.append("{\"key\":\"after.sales.service.type.unsupported\",\"en\":\"After-sales service type is not supported\",\"ru\":\"Тип послепродажного обслуживания не поддерживается\",\"kk\":\"Сатудан кейінгі қызмет түріне қолдау көрсетілмейді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"sub.order.info.required\",\"en\":\"Sub-order information field is required\",\"ru\":\"Поле \\\"\\\"Информации о подзаказе\\\"\\\" обязательно для заполнения\",\"kk\":\"Қосалқы тапсырыс туралы ақпарат өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қосалқы тапсырыс туралы ақпарат\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"single.sub.order.after.sales\",\"en\":\"After-sales service request must be submitted for a single sub-order\",\"ru\":\"Заявка на послепродажное обслуживание должна быть подана для одного подзаказа\",\"kk\":\"Сатудан кейінгі қызмет көрсету өтінімі бір қосалқы тапсырыс үшін берілуі керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"refund.total.amount.required\",\"en\":\"Total return amount field is required\",\"ru\":\"Поле \\\"\\\"общая сумма возврата\\\"\\\" обязательно для заполнения\",\"kk\":\"Жалпы қайтару сомасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Жалпы қайтару сомасы\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"refund.total.amount.not.less\",\"en\":\"Total return amount cannot be less than\",\"ru\":\"Общая сумма возврата не может быть меньше\",\"kk\":\"Қайтарудың жалпы сомасы аз бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"refund.reason\",\"en\":\"Refund Reason\",\"ru\":\"Причина возврата\",\"kk\":\"Қайтару себебі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"proof.image.format.invalid\",\"en\":\"Incorrect image address format for verification\",\"ru\":\"Неправильный формат адреса изображения для проверки\",\"kk\":\"Тексеруге арналған сурет мекенжайының пішімі дұрыс емес \",\"ru_new\":\"\",\"kk new\":\"Тексеруге арналған сурет мекенжайының форматты дұрыс емес \"}\n" +
                "{\"key\":\"add.list.required\",\"en\":\"Add List Cannot Be Empty\",\"ru\":\"Поле \\\"\\\"Добавить список\\\"\\\" не может быть пустым\",\"kk\":\"Тізім қосу өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тізім қосу\\\"\\\" өрісі бос бола алмайды\"}\n" +
                "{\"key\":\"service.evaluation.required\",\"en\":\"Service rating field is required\",\"ru\":\"Поле \\\"\\\"Оценка сервиса\\\"\\\" обязательно для заполнения\",\"kk\":\"Сервисті бағалау өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сервисті бағалау\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"service.evaluation.score\",\"en\":\"Service rating\",\"ru\":\"Оценка сервиса\",\"kk\":\"Сервисті бағалау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"logistics.evaluation.required\",\"en\":\"Logistics rating field is required\",\"ru\":\"Поле \\\"\\\"Оценка логистики\\\"\\\" обязательно для заполнения\",\"kk\":\"Логистикалық бағалау өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Логистикалық бағалау\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"logistics.evaluation.score\",\"en\":\"Logistics rating\",\"ru\":\"Оценка логистики\",\"kk\":\"Логистиканы бағалау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"sub.order.evaluation.required\",\"en\":\"Sub-order rating field is required\",\"ru\":\"Поле \\\"\\\"Оценка подзаказа\\\"\\\" обязательно для заполнения\",\"kk\":\"Қосалқы тапсырысты бағалау өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қосалқы тапсырысты бағалау\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"refund.number.required\",\"en\":\"Return number field is required\",\"ru\":\"Поле \\\"\\\"Номер возврата\\\"\\\" обязательно для заполнения\",\"kk\":\"Қайтару нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қайтару нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"refund.message.required\",\"en\":\"Return message field is required\",\"ru\":\"Поле \\\"\\\"Сообщение о возврате\\\"\\\" обязательно для заполнения\",\"kk\":\"Қайтару туралы хабарлама өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қайтару туралы хабарлама\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"logistics.info.required\",\"en\":\"Logistics information field is required\",\"ru\":\"Поле \\\"\\\"Логистическая информация\\\"\\\" обязательно для заполнения\",\"kk\":\"Логистикалық ақпарат өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Логистикалық ақпарат\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"select.delete.product\",\"en\":\"Please select items to remove\",\"ru\":\"Пожалуйста, выберите товары для удаления\",\"kk\":\"Жоюға арналған тауарларды таңдауыңызды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.header.info.required\",\"en\":\"Invoice header information field is required\",\"ru\":\"Поле \\\"\\\"Информация о заголовке инвойса\\\"\\\" обязательно для заполнения\",\"kk\":\"Инвойс тақырыбы туралы ақпарат өрісі міндетті түрде толтырылады\",\"ru_new\":\"Поле \\\"\\\"Информация о заголовке счета-фактуры\\\"\\\" обязательно для заполнения\",\"kk new\":\"\\\"\\\"Шот-фактура тақырыбы туралы ақпарат\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"after.sales.main.order\",\"en\":\"Main order for after-sales service\",\"ru\":\"Основной заказ для послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі қызмет көрсетке арналған негізгі тапсырыс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"stage.number.required\",\"en\":\"Stage number field is required\",\"ru\":\"Поле \\\"\\\"Номер этапа\\\"\\\" обязательно для заполнения\",\"kk\":\"Кезең нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Кезең нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"stage.number.pos.int\",\"en\":\"Stage number must be a positive integer\",\"ru\":\"Номер этапа должен быть положительным целым числом\",\"kk\":\"Кезең нөмірі оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.product.info.required\",\"en\":\"Ordered product information field is required\",\"ru\":\"Поле \\\"\\\"Информация о заказываемых товарах\\\"\\\" обязательно для заполнения\",\"kk\":\"Тапсырыс берілген тауарлар туралы ақпарат өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тапсырыс берілген тауарлар туралы ақпарат\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"after.sales.type.required\",\"en\":\"After-sales service type field is required\",\"ru\":\"Поле \\\"\\\"тип послепродажного обслуживания\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатудан кейінгі қызмет түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатудан кейінгі қызмет түрі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"after.sales.type.unsupported\",\"en\":\"After-sales service type is not supported\",\"ru\":\"Тип послепродажного обслуживания не поддерживается\",\"kk\":\"Сатудан кейінгі қызмет түріне қолдау көрсетілмейді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"please.confirm\",\"en\":\"Please confirm\",\"ru\":\"Пожалуйста, подтвердите\",\"kk\":\"Растауыңызды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n");
        sb.append( "{\"key\":\"after.sales.order\",\"en\":\"After-sales service order\",\"ru\":\"Заказ на послепродажное обслуживание\",\"kk\":\"Сатудан кейінгі қызметке тапсырыс беру\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.required\",\"en\":\"Product field cannot be empty\",\"ru\":\"Поле \\\"\\\"Товар\\\"\\\" не может быть пустым\",\"kk\":\"Тауар өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тауар\\\"\\\" өрісі бос бола алмайды\"}\n" +
                "{\"key\":\"valid.days.from.purchase\",\"en\":\"Valid for days from purchase\",\"ru\":\"Действителен в течение дней с момента покупки\",\"kk\":\"Сатып алғаннан кейінгі күндер ішінде жарамды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"valid.from.purchase.to.long.term\",\"en\":\"Valid indefinitely from the moment of purchase\",\"ru\":\"Действителен с момента покупки на неопределенный срок\",\"kk\":\"Сатып алған сәттен бастап белгісіз мерзімге жарамды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"valid.until\",\"en\":\"Valid Until\",\"ru\":\"Действителен до\",\"kk\":\"Дейін жарамды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller.signed\",\"en\":\"Seller Signed\",\"ru\":\"Продавец подписал\",\"kk\":\"Сатушы қол қойды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller.received\",\"en\":\"Seller Received\",\"ru\":\"Продавец получил\",\"kk\":\"Сатушы алды\",\"ru_new\":\"\",\"kk new\":\"Сатушы қабылдады\"}\n" +
                "{\"key\":\"seller.sent\",\"en\":\"Seller Sent\",\"ru\":\"Продавец отправил\",\"kk\":\"Сатушы жіберді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.signed\",\"en\":\"Buyer Signed\",\"ru\":\"Покупатель подписал\",\"kk\":\"Сатып алушы қол қойды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.sent\",\"en\":\"Buyer Sent\",\"ru\":\"Покупатель отправил\",\"kk\":\"Сатып алушы жіберді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.received\",\"en\":\"Buyer Received\",\"ru\":\"Покупатель получил\",\"kk\":\"Сатып алушы алды\",\"ru_new\":\"\",\"kk new\":\"Сатып алушы қабылдады\"}\n" +
                "{\"key\":\"public.payment\",\"en\":\"Public Payment\",\"ru\":\"Публичный платеж\",\"kk\":\"Жария төлем\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.period.payment\",\"en\":\"Payment on a settlement period basis\",\"ru\":\"Оплата на условиях расчетного периода\",\"kk\":\"Есеп айырысу кезеңінің шарттарында төлеу\",\"ru_new\":\"\",\"kk new\":\"Есеп айырысу мерзімі негізінде төлеу\"}\n" +
                "{\"key\":\"gift\",\"en\":\"Gift\",\"ru\":\"Подарок\",\"kk\":\"Сыйлық\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"add.to.cart.fail\",\"en\":\"Failed to add to cart\",\"ru\":\"Ошибка добавления в корзину\",\"kk\":\"Себетке қосу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.query.fail\",\"en\":\"Failed to request cart\",\"ru\":\"Ошибка запроса корзины\",\"kk\":\"Себет сұранысының қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.not.add.to.cart\",\"en\":\"Product cannot be added to the cart\",\"ru\":\"Товар не может быть добавлен в корзину\",\"kk\":\"Тауарды себетке қосу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.modify.fail\",\"en\":\"Failed to modify cart\",\"ru\":\"Ошибка изменения корзины\",\"kk\":\"Себетті өзгерту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.product.delete.fail\",\"en\":\"Failed to remove product from cart\",\"ru\":\"Ошибка удаления товара из корзины\",\"kk\":\"Себеттен тауарды жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.price.calculate.fail\",\"en\":\"Failed to calculate cart price\",\"ru\":\"Ошибка расчета цены корзины\",\"kk\":\"Себет бағасын есептеу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.confirm.fail\",\"en\":\"Failed to confirm order\",\"ru\":\"Ошибка подтверждения заказа\",\"kk\":\"Тапсырысты растау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.create.fail\",\"en\":\"Failed to create order\",\"ru\":\"Ошибка создания заказа\",\"kk\":\"Тапсырыс жасау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.cancel.fail\",\"en\":\"Failed to cancel order\",\"ru\":\"Ошибка отмены заказа\",\"kk\":\"Тапсырыстан бас тарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.delete.fail\",\"en\":\"Failed to delete order\",\"ru\":\"Ошибка удаления заказа\",\"kk\":\"Тапсырысты жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.confirm.receipt.fail\",\"en\":\"Failed to confirm order receipt\",\"ru\":\"Ошибка подтверждения получения заказа\",\"kk\":\"Тапсырысты алуды растау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.details.query.fail\",\"en\":\"Failed to query order details\",\"ru\":\"Ошибка запроса деталей заказа\",\"kk\":\"Тапсырыс мәліметтерін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.list.query.fail\",\"en\":\"Failed to query order list\",\"ru\":\"Ошибка запроса списка заказов\",\"kk\":\"Тапсырыс тізімін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.not.support.delivery\",\"en\":\"Delivery address is not supported\",\"ru\":\"Адрес доставки не поддерживается\",\"kk\":\"Жеткізу мекенжайына қолдау көрсетілмейді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"select.delivery.address\",\"en\":\"Please select another delivery address\",\"ru\":\"Пожалуйста, выберите другой адрес доставки\",\"kk\":\"Басқа жеткізу мекенжайын таңдауыңызды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"select.product.to.buy\",\"en\":\"Please select items to purchase\",\"ru\":\"Пожалуйста, выберите товары для покупки\",\"kk\":\"Сатып алатын тауарларды таңдаңызды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"single.purchase.limit\",\"en\":\"Cannot purchase more than the specified quantity of items from one seller at a time\",\"ru\":\"Нельзя приобрести более указанного количества товаров у одного продавца за раз\",\"kk\":\"Бір уақытта бір сатушыдан көрсетілген тауарлар санынан артық сатып алуға болмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"load.fail\",\"en\":\"Upload failed\",\"ru\":\"Ошибка загрузки\",\"kk\":\"Жүктеу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.status.load.fail\",\"en\":\"Error uploading payment status\",\"ru\":\"Ошибка загрузки статуса оплаты\",\"kk\":\"Төлем мәртебесін жүктеу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"check.order.list\",\"en\":\"Please check the order list\",\"ru\":\"Пожалуйста, проверьте список заказов\",\"kk\":\"Тапсырыстар тізімін тексеруіңізді өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.fail\",\"en\":\"Payment failed\",\"ru\":\"Ошибка оплаты\",\"kk\":\"Төлем қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.list.query.fail\",\"en\":\"Failed to query the return list\",\"ru\":\"Ошибка запроса списка возвратов\",\"kk\":\"Қайтару тізімін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.details.query.fail\",\"en\":\"Failed to query return details\",\"ru\":\"Ошибка запроса деталей возврата\",\"kk\":\"Қайтару мәліметтерін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.apply.submit.fail\",\"en\":\"Failed to submit return request\",\"ru\":\"Ошибка подачи заявки на возврат\",\"kk\":\"Қайтаруға өтініш беру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.cancel.fail\",\"en\":\"Failed to cancel the return\",\"ru\":\"Ошибка отмены возврата\",\"kk\":\"Қайтарудан бас тарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.reason.query.fail\",\"en\":\"Failed to retrieve return reason\",\"ru\":\"Ошибка запроса причины возврата\",\"kk\":\"Қайтару себебін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"mail.submit.fail\",\"en\":\"Failed to submit mail\",\"ru\":\"Ошибка отправки почты\",\"kk\":\"Поштаны жіберу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.not.returnable\",\"en\":\"Order cannot be returned\",\"ru\":\"Заказ не подлежит возврату\",\"kk\":\"Тапсырыс қайтарылмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.confirm.refund.fail\",\"en\":\"Failed to confirm refund by buyer\",\"ru\":\"Ошибка подтверждения возврата покупателем\",\"kk\":\"Сатып алушының қайтаруды растау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.evaluation.fail\",\"en\":\"Failed to evaluate order\",\"ru\":\"Ошибка оценки заказа\",\"kk\":\"Тапсырысты бағалау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"logistics.query.fail\",\"en\":\"Failed to query logistics\",\"ru\":\"Ошибка запроса логистики\",\"kk\":\"Логистикалық сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.query.fail\",\"en\":\"Failed to query product\",\"ru\":\"Ошибка запроса товара\",\"kk\":\"Тауарды сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.query.fail\",\"en\":\"Failed to query address\",\"ru\":\"Ошибка запроса адреса\",\"kk\":\"Мекенжайды сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.qty.query.fail\",\"en\":\"Failed to query order quantity\",\"ru\":\"Ошибка запроса количества заказов\",\"kk\":\"Тапсырыс санын сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"batch.query.fail\",\"en\":\"Failed to query in bulk\",\"ru\":\"Ошибка массового запроса\",\"kk\":\"Жаппай сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.repeated.evaluate\",\"en\":\"Cannot re-evaluate an already evaluated order\",\"ru\":\"Невозможно повторно оценить уже оцененный заказ\",\"kk\":\"Бағаланып қойған тапсырысты қайта бағалау мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.not.exist\",\"en\":\"Product does not exist\",\"ru\":\"Продукт не существует\",\"kk\":\"Өнім жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.product.query.fail\",\"en\":\"Failed to query cart product\",\"ru\":\"Ошибка при запросе товара в корзине\",\"kk\":\"Себеттегі тауарды сұраудағы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.apply.fail\",\"en\":\"Failed to apply for invoice\",\"ru\":\"Ошибка при подаче заявки на счет-фактуру\",\"kk\":\"Шот-фактураға өтініш беру кезінде қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.get.fail\",\"en\":\"Failed to retrieve invoice\",\"ru\":\"Ошибка при получении счета-фактуры\",\"kk\":\"Шот-фактураны алу кезінде қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"unused\",\"en\":\"Unused\",\"ru\":\"Не использован\",\"kk\":\"Пайдаланылмаған\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"redeemed\",\"en\":\"Activated\",\"ru\":\"Активирован\",\"kk\":\"Іске қосылды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"disabled\",\"en\":\"Disabled\",\"ru\":\"Отключен\",\"kk\":\"Ажыратылған\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"expired\",\"en\":\"Expired\",\"ru\":\"Истёк\",\"kk\":\"Мерзімі аяқталды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"pending\",\"en\":\"Pending\",\"ru\":\"Ожидание\",\"kk\":\"Күту\",\"ru_new\":\"\",\"kk new\":\"Күтілуде\"}\n" +
                "{\"key\":\"all\",\"en\":\"All\",\"ru\":\"Все\",\"kk\":\"Барлығы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" );
        sb.append(          "{\"key\":\"pending.payment\",\"en\":\"Pending Payment\",\"ru\":\"В ожидании оплаты\",\"kk\":\"Төлемді күту\",\"ru_new\":\"\",\"kk new\":\"Төлем күтілуде\"}\n" +
                "{\"key\":\"pending.shipment\",\"en\":\"Pending Shipment\",\"ru\":\"В ожидании отправки\",\"kk\":\"Жөнелтуді күту\",\"ru_new\":\"\",\"kk new\":\"Жөнелту күтілуде\"}\n" +
                "{\"key\":\"pending.receipt\",\"en\":\"Pending Receipt\",\"ru\":\"В ожидании получения\",\"kk\":\"Алуды күту\",\"ru_new\":\"\",\"kk new\":\"Қабылдауды күтуде\"}\n" +
                "{\"key\":\"pending.evaluation\",\"en\":\"Pending Evaluation\",\"ru\":\"В ожидании оценки\",\"kk\":\"Бағалауды күту\",\"ru_new\":\"\",\"kk new\":\"Бағалауды күтуде\"}\n" +
                "{\"key\":\"main.order.evaluation\",\"en\":\"Main Order Evaluation of Seller Service and Logistics\",\"ru\":\"Оценка основного заказа по обслуживанию продавца и логистике\",\"kk\":\"Сатушыға қызмет көрсету және логистика бойынша негізгі тапсырысты бағалау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.completed\",\"en\":\"Payment Completed\",\"ru\":\"Оплата завершена\",\"kk\":\"Төлем аяқталды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.closed\",\"en\":\"Payment Closed\",\"ru\":\"Оплата закрыта\",\"kk\":\"Төлем жабық\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.confirmed.payment\",\"en\":\"Buyer Confirmed Payment\",\"ru\":\"Покупатель подтвердил оплату\",\"kk\":\"Сатып алушы төлемді растады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"overdue.unpaid\",\"en\":\"Overdue Unpaid\",\"ru\":\"Просроченная неоплата\",\"kk\":\"Кешіктірілген төленбеген төлем\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"special.invoice.applied\",\"en\":\"Special Invoice Applied\",\"ru\":\"Заявка на специальную счет-фактуру подана\",\"kk\":\"Арнайы шот-фактураға өтінім берілді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"order.payment.insert.receipt.fail\",\"en\":\"Order Payment Insert Receipt Claim Query Order Failed\",\"ru\":\"Ошибка при запросе о добавлении чека по оплате заказа\",\"kk\":\"Тапсырысты төлеу чегін қосуды сұраған кезде қате пайда болды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"add.delivery.address\",\"en\":\"Please Add Delivery Address\",\"ru\":\"Пожалуйста, добавьте адрес доставки\",\"kk\":\"Жеткізу мекенжайын қосуыңызды өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.in.progress\",\"en\":\"Payment in Progress\",\"ru\":\"Оплата в процессе\",\"kk\":\"Процесс барысында төлеу\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"no.win\",\"en\":\"No Win\",\"ru\":\"Нет выигрыша\",\"kk\":\"Жеңіс жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"unlucky\",\"en\":\"Unlucky\",\"ru\":\"Не удалось\",\"kk\":\"Сәтсіз аяқталды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"not.winning\",\"en\":\"Not Winning\",\"ru\":\"Не выиграл\",\"kk\":\"Жеңген жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"congratulations\",\"en\":\"Congratulations\",\"ru\":\"Поздравляем\",\"kk\":\"Құттықтаймыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"event.not.exist\",\"en\":\"Event Does Not Exist\",\"ru\":\"Событие не существует\",\"kk\":\"Оқиға жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.event.draw.times.error\",\"en\":\"Points Event Draw Times Query Error\",\"ru\":\"Ошибка при запросе количества попыток участия в розыгрыше в рамках акции по баллам\",\"kk\":\"Ұпай бойынша науқан аясында ұтыс ойынына қатысу әрекеттерінің санын сұраудағы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.receive.success\",\"en\":\"Coupon Received Successfully\",\"ru\":\"Купон успешно получен\",\"kk\":\"Купон сәтті алынды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.coupon\",\"en\":\"Points Coupon\",\"ru\":\"Купон за баллы\",\"kk\":\"Ұпайлар үшін купондар\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"event\",\"en\":\"Event\",\"ru\":\"Событие\",\"kk\":\"Оқиға\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.freeze.fail\",\"en\":\"Points Freeze Failed\",\"ru\":\"Ошибка заморозки баллов\",\"kk\":\"Ұпайларды қатыру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.deduct.fail\",\"en\":\"Points Deduct Failed\",\"ru\":\"Ошибка списания баллов\",\"kk\":\"Ұпайларды есептен шығару қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.unfreeze.fail\",\"en\":\"Points Unfreeze Failed\",\"ru\":\"Ошибка разморозки баллов\",\"kk\":\"Ұпайларды жібіту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.receive.fail\",\"en\":\"Coupon Receive Failed\",\"ru\":\"Ошибка получения купона\",\"kk\":\"Купонды алу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.query.fail\",\"en\":\"Coupon Query Failed\",\"ru\":\"Ошибка при запросе купона\",\"kk\":\"Купон сұраудағы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.coupon.price.empty\",\"en\":\"Points Coupon Price is Empty\",\"ru\":\"Цена купона за баллы пуста\",\"kk\":\"Ұпайлар үшін купон бағасы бос\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.coupon.info.error\",\"en\":\"Points Coupon Information Error\",\"ru\":\"Ошибка информации о купоне за баллы\",\"kk\":\"Ұпай купоны туралы ақпарат қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"idempotent.coupon.query.fail\",\"en\":\"Idempotent Query of Received Coupon Failed\",\"ru\":\"Ошибка идемпотентного запроса на полученный купон\",\"kk\":\"Алынған купонға идемпотентті сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"receiver.name.required\",\"en\":\"Receiver Name Required\",\"ru\":\"Укажите имя получателя\",\"kk\":\"Алушының атын көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"Қабылдаушының атын көрсетіңіз\"}\n" +
                "{\"key\":\"receiver.name.len.exceed\",\"en\":\"Receiver Name Length Exceeds Characters\",\"ru\":\"Длина имени получателя превышает допустимое количество символов\",\"kk\":\"Алушы атының ұзындығы таңбалардың рұқсат етілген санынан асады\",\"ru_new\":\"\",\"kk new\":\"Қабылдаушының атының ұзындығы таңбалардың рұқсат етілген санынан асады\"}\n" +
                "{\"key\":\"receiver.phone.required\",\"en\":\"Receiver Phone Number Required\",\"ru\":\"Укажите номер телефона получателя\",\"kk\":\"Алушының телефон нөмірін көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"Қабылдаушының телефон нөмірін көрсетіңіз\"}\n" +
                "{\"key\":\"province\",\"en\":\"Province\",\"ru\":\"Область\",\"kk\":\"Облыс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"city\",\"en\":\"City\",\"ru\":\"Город\",\"kk\":\"Қала\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"district\",\"en\":\"District\",\"ru\":\"Район\",\"kk\":\"Аудан\",\"ru_new\":\"\",\"kk new\":\"\"}\n" );
        sb.append("{\"key\":\"province.required\",\"en\":\"Specify region\",\"ru\":\"Укажите область\",\"kk\":\"Аймақты көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"city.required\",\"en\":\"Specify city\",\"ru\":\"Укажите город\",\"kk\":\"Қаланы көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"district.required\",\"en\":\"Specify district\",\"ru\":\"Укажите район\",\"kk\":\"Ауданды көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.details.required\",\"en\":\"Specify address details\",\"ru\":\"Укажите детали адреса\",\"kk\":\"Мекенжай мәліметтерін көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.details.len.exceed\",\"en\":\"Address details length exceeds the allowed character limit\",\"ru\":\"Длина деталей адреса превышает допустимое количество символов\",\"kk\":\"Мекенжай мәліметтерінің ұзындығы таңбалардың рұқсат етілген санынан асады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"original.password.required\",\"en\":\"Specify the original password\",\"ru\":\"Укажите исходный пароль\",\"kk\":\"Бастапқы құпия сөзді көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"first.set.any.value\",\"en\":\"Any value can be entered initially\",\"ru\":\"При первом вводе может быть любое значение\",\"kk\":\"Бірінші енгізу кезінде кез келген мән болуы мүмкін\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"update.user.info.not.all.empty\",\"en\":\"No field can be empty when updating user information\",\"ru\":\"При обновлении информации о пользователе ни одно из полей не может быть пустым\",\"kk\":\"Пайдаланушы туралы ақпаратты жаңарту кезінде өрістердің ешқайсысы бос болмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"update.user.language.not.empty\",\"en\":\"Specify user language for update\",\"ru\":\"Укажите язык пользователя для обновления\",\"kk\":\"Жаңарту үшін пайдаланушының тілін көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.header.name.invalid\",\"en\":\"Invoice Header Name Invalid\",\"ru\":\"Неправильное название заголовка счета\",\"kk\":\"Шот тақырыбының атауы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"please.reenter\",\"en\":\"Please Re-enter\",\"ru\":\"Пожалуйста, введите заново\",\"kk\":\"Қайта енгізуіңізді өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.avatar.required\",\"en\":\"Specify user avatar\",\"ru\":\"Укажите аватар пользователя\",\"kk\":\"Пайдаланушының аватарын көрсетіңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon\",\"en\":\"Coupon\",\"ru\":\"Купон\",\"kk\":\"Купон\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"join.member.not.all.empty\",\"en\":\"No field can be empty when joining membership\",\"ru\":\"При присоединении к членству ни одно из полей не может быть пустым\",\"kk\":\"Мүшелікке қосылған кезде өрістердің ешқайсысы бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.join.member\",\"en\":\"Cannot Join Member Simultaneously\",\"ru\":\"Невозможно одновременно присоединиться к членству\",\"kk\":\"Бір уақытта мүшелікке қосылу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address\",\"en\":\"Address\",\"ru\":\"Адрес\",\"kk\":\"Мекенжайы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"must.be.pos.int\",\"en\":\"Must be a positive integer\",\"ru\":\"Должно быть положительное целое число\",\"kk\":\"Оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller\",\"en\":\"Seller\",\"ru\":\"Продавец\",\"kk\":\"Сатушы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"password.incorrect\",\"en\":\"Incorrect password\",\"ru\":\"Неверный пароль\",\"kk\":\"Құпия сөз қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.info.update.fail\",\"en\":\"User Information Update Failed\",\"ru\":\"Ошибка обновления информации о пользователе\",\"kk\":\"Пайдаланушы туралы ақпаратты жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.level.config.query.fail\",\"en\":\"User Level Configuration Query Failed\",\"ru\":\"Ошибка при запросе конфигурации уровня пользователя\",\"kk\":\"Пайдаланушы деңгейінің конфигурациясын сұрау кезіндегі қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.level.query.fail\",\"en\":\"User Level Query Failed\",\"ru\":\"Ошибка при запросе уровня пользователя\",\"kk\":\"Пайдаланушының деңгейін сұраудағы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"new.user.check.fail\",\"en\":\"New User Check Failed\",\"ru\":\"Ошибка проверки нового пользователя\",\"kk\":\"Жаңа пайдаланушыны тексеру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"delivery.address.save.fail\",\"en\":\"Save Delivery Address Failed\",\"ru\":\"Ошибка сохранения адреса доставки\",\"kk\":\"Жеткізу мекенжайын сақтау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"delivery.address.delete.fail\",\"en\":\"Delete Delivery Address Failed\",\"ru\":\"Ошибка удаления адреса доставки\",\"kk\":\"Жеткізу мекенжайын жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"set.default.delivery.address.fail\",\"en\":\"Set Default Delivery Address Failed\",\"ru\":\"Ошибка установки адреса доставки по умолчанию\",\"kk\":\"Әдепкі жеткізу мекенжайын орнату қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.add.fail\",\"en\":\"Add Invoice Failed\",\"ru\":\"Ошибка добавления счета\",\"kk\":\"Шотты қосу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.delete.fail\",\"en\":\"Delete Invoice Failed\",\"ru\":\"Ошибка удаления счета\",\"kk\":\"Шотты жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.edit.fail\",\"en\":\"Edit Invoice Failed\",\"ru\":\"Ошибка редактирования счета\",\"kk\":\"Шотты өңдеу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.query.fail\",\"en\":\"Query Invoice Failed\",\"ru\":\"Ошибка запроса счета\",\"kk\":\"Шотты сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"level.benefit.not.exist\",\"en\":\"Level Benefit Does Not Exist\",\"ru\":\"Преимущество уровня не существует\",\"kk\":\"Деңгейдің артықшылығы жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"please.select.other.benefit\",\"en\":\"Please Select Other Benefit\",\"ru\":\"Выберите другое преимущество\",\"kk\":\"Басқа артықшылықты таңдаңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"benefit.already.received\",\"en\":\"Benefit Already Received\",\"ru\":\"Преимущество уже получено\",\"kk\":\"Артықшылық алынып қойған\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"request.too.fast\",\"en\":\"Too frequent requests\",\"ru\":\"Слишком частые запросы\",\"kk\":\"Сұраулар тым жиі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"event.participated.or.not.targeted\",\"en\":\"Already participated in the event or not in the target group\",\"ru\":\"Уже участвовал в событии или не принадлежит к целевой группе\",\"kk\":\"Оқиғаға бұрын қатысқан немесе мақсатты топқа жатпайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"current.coupon.not.configured\",\"en\":\"Current coupon is not configured\",\"ru\":\"Текущий купон не настроен\",\"kk\":\"Ағымдағы купон реттелмеген\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"please.retry.after.config\",\"en\":\"Try again after configuration\",\"ru\":\"Повторите попытку после настройки\",\"kk\":\"Орнатқаннан кейін әрекетті қайталаңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"gift.receive.success\",\"en\":\"Gift successfully received\",\"ru\":\"Подарок успешно получен\",\"kk\":\"Сыйлық сәтті алынды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"each.coupon.limit.one\",\"en\":\"Each coupon type is limited to one instance\",\"ru\":\"Каждый тип купона ограничен одним экземпляром\",\"kk\":\"Купонның әр түрі бір данамен шектеледі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"partial.gift.receive.success\",\"en\":\"Partial gift successfully received\",\"ru\":\"Частичный подарок успешно получен\",\"kk\":\"Ішінара сыйлық сәтті алынды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"please.retry.later\",\"en\":\"Please try again later\",\"ru\":\"Повторите попытку позже\",\"kk\":\"Кейінірек қайталап көріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"already.participated.event\",\"en\":\"Already participated in the event\",\"ru\":\"Уже участвовал в событии\",\"kk\":\"Оқиғаға бұрын қатысқан\",\"ru_new\":\"\",\"kk new\":\"\"}\n" );
        sb.append(   "{\"key\":\"thank.you.for.your.attention\",\"en\":\"Thank you for your attention\",\"ru\":\"Спасибо за ваше внимание\",\"kk\":\"Назарларыңызға рахмет\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.level.not.satisfy\",\"en\":\"User level does not meet conditions for receiving benefits\",\"ru\":\"Уровень пользователя не соответствует условиям для получения преимущества\",\"kk\":\"Пайдаланушы деңгейі артықшылықты алу шарттарына сәйкес келмейді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.join.store.member\",\"en\":\"Cannot Join Store Member\",\"ru\":\"Невозможно присоединиться к членству магазина\",\"kk\":\"Дүкен мүшелігіне қосылу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.join.vip.customer\",\"en\":\"Cannot Join VIP Customers\",\"ru\":\"Невозможно присоединиться к клиентам VIP\",\"kk\":\"VIP клиенттерге қосылу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"store.not.exist\",\"en\":\"Store does not exist\",\"ru\":\"Магазин не существует\",\"kk\":\"Дүкен жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.details.receive.scene\",\"en\":\"Product details and other receiving scenarios\",\"ru\":\"Детали продукта и другие сцены получения\",\"kk\":\"Өнім туралы мәліметтер және басқа алу көріністері\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"external.share.receive.scene\",\"en\":\"Receiving scene through external link\",\"ru\":\"Сцена получения через внешнюю ссылку\",\"kk\":\"Сыртқы сілтеме арқылы алу көрінісі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"level.benefit.receive.scene\",\"en\":\"Receiving level benefit scene\",\"ru\":\"Сцена получения преимущества уровня\",\"kk\":\"Деңгейдің артықшылығын алу көрінісі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.not.exist\",\"en\":\"User does not exist\",\"ru\":\"Пользователь не существует\",\"kk\":\"Пайдаланушы жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"receive.success\",\"en\":\"Successfully received\",\"ru\":\"Успешно получено\",\"kk\":\"Сәтті алынды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"receive.fail\",\"en\":\"Receive Failed\",\"ru\":\"Ошибка получения\",\"kk\":\"Алу қатесі\",\"ru_new\":\"\",\"kk new\":\"Қабылдау кезіндегі қате\"}\n" +
                "{\"key\":\"already.received.coupon\",\"en\":\"Already Received Coupon\",\"ru\":\"Купон уже использован\",\"kk\":\"Купон қолданылып қойған\",\"ru_new\":\"\",\"kk new\":\"Купон қолданылған\"}\n" +
                "{\"key\":\"or\",\"en\":\"Or\",\"ru\":\"Или\",\"kk\":\"Немесе\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"business.type.required\",\"en\":\"\\\"\\\"Business type\\\"\\\" field is required\",\"ru\":\"Поле \\\"\\\"Тип бизнеса\\\"\\\" обязательно для заполнения\",\"kk\":\"Бизнес түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Бизнес түрі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"file.name.required\",\"en\":\"\\\"\\\"File name\\\"\\\" field is required\",\"ru\":\"Поле \\\"\\\"Имя файла\\\"\\\" обязательно для заполнения\",\"kk\":\"Файл атауы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Файл атауы\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"specification.not.selected\",\"en\":\"Specification not selected\",\"ru\":\"Спецификация не выбрана\",\"kk\":\"Сипаттама таңдалмаған\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.province\",\"en\":\"Address Province\",\"ru\":\"Провинция\",\"kk\":\"Провинция\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.city\",\"en\":\"Address City\",\"ru\":\"Город\",\"kk\":\"Қала\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.district\",\"en\":\"Address District\",\"ru\":\"Район\",\"kk\":\"Аудан\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"address.province.city.district.name.required\",\"en\":\"Address Province, City, District Name Required\",\"ru\":\"Поле \\\"\\\"Название провинции, города и района адреса\\\"\\\" обязательно для заполнения\",\"kk\":\"Провинцияның, қаланың және мекенжайдың атауы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Провинцияның, қаланың және мекенжайдың атауы\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"not.correct\",\"en\":\"Not Correct\",\"ru\":\"Некорректно\",\"kk\":\"Дұрыс емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"flash.sale\",\"en\":\"Flash Sale\",\"ru\":\"Распродажа\",\"kk\":\"Жаппай сату\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"delivery.address.not.exist\",\"en\":\"Delivery Address Does Not Exist\",\"ru\":\"Адрес доставки не существует\",\"kk\":\"Жеткізу мекенжайы жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"file.upload.fail\",\"en\":\"File Upload Failed\",\"ru\":\"Ошибка загрузки файла\",\"kk\":\"Файлды жүктеу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"check.business.type\",\"en\":\"Please Check Business Type\",\"ru\":\"Проверьте тип бизнеса\",\"kk\":\"Бизнес түрін тексеріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"balance.payment.time\",\"en\":\"Balance Payment Time\",\"ru\":\"Время оплаты остатка\",\"kk\":\"Қалдықты төлеу уақыты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"balance.amount.confirmed.within.days\",\"en\":\"Balance Amount Confirmed Within Days\",\"ru\":\"Остаток суммы подтверждается в течение дней\",\"kk\":\"Соманың қалдығы күндер ішінде расталады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.product.list.query.fail\",\"en\":\"Points Product List Query Failed\",\"ru\":\"Ошибка запроса списка товаров за баллы\",\"kk\":\"Ұпайлар үшін берілетін тауарлар тізімін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.product.details.query.fail\",\"en\":\"Points Product Details Query Failed\",\"ru\":\"Ошибка запроса деталей товара за баллы\",\"kk\":\"Ұпайлар үшін берілетін тауарлар мәлеметтерін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.list.query.fail\",\"en\":\"Coupon List Query Failed\",\"ru\":\"Ошибка запроса списка купонов\",\"kk\":\"Купондар тізімін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"all.products.available\",\"en\":\"All Products Available\",\"ru\":\"Все товары доступны\",\"kk\":\"Барлық тауарлар қол жетімді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"specified.products.available\",\"en\":\"Specified Products Available\",\"ru\":\"Указанные товары доступны\",\"kk\":\"Көрсетілген тауарлар қол жетімді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"specified.group.available\",\"en\":\"Specified Group Available\",\"ru\":\"Указанная группа доступна\",\"kk\":\"Көрсетілген топ қол жетімді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.details.query.fail\",\"en\":\"Coupon Details Query Failed\",\"ru\":\"Ошибка запроса деталей купона\",\"kk\":\"Купон мәліметтерін сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"after.purchase\",\"en\":\"After Purchase\",\"ru\":\"После покупки\",\"kk\":\"Сатып алғаннан кейін\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"after.purchase.to\",\"en\":\"After purchase until\",\"ru\":\"После покупки до\",\"kk\":\"Дейін сатып алғаннан кейін\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"pagination.params\",\"en\":\"Pagination Parameters\",\"ru\":\"Параметры постраничного отображения\",\"kk\":\"Беттік бейнелеу параметрлері\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"can.not.greater.than\",\"en\":\"Cannot Exceed\",\"ru\":\"Не может превышать\",\"kk\":\"Аса алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"dictionary\",\"en\":\"Dictionary\",\"ru\":\"Справочник\",\"kk\":\"Анықтамалық\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.be.empty\",\"en\":\"Cannot Be Empty\",\"ru\":\"Не может быть пустым\",\"kk\":\"Бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"remark.required\",\"en\":\"\\\"\\\"Remark\\\"\\\" field cannot be empty\",\"ru\":\"Поле \\\"\\\"Комментарий\\\"\\\" не может быть пустым\",\"kk\":\"Түсініктеме өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Түсініктеме\\\"\\\" өрісі бос бола алмайды\"}\n" +
                "{\"key\":\"required\",\"en\":\"Required\",\"ru\":\"Обязательно для заполнения\",\"kk\":\"Толтыру үшін міндетті\",\"ru_new\":\"\",\"kk new\":\"Толтыру міндетті\"}\n" +
                "{\"key\":\"parent.node\",\"en\":\"Parent Node\",\"ru\":\"Родительский узел\",\"kk\":\"Ата-ана түйіні\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"category.name.len\",\"en\":\"Category name length\",\"ru\":\"Длина названия категории\",\"kk\":\"Санат атауының ұзындығы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"characters\",\"en\":\"Characters\",\"ru\":\"Символы\",\"kk\":\"Таңбалар\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"search.keywords.required\",\"en\":\"\\\"\\\"Search keywords\\\"\\\" field is required\",\"ru\":\"Поле \\\"\\\"Ключевые слова для поиска\\\"\\\" обязательно для заполнения\",\"kk\":\"Іздеуге арналған кілттік сөздер өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Іздеуге арналған кілттік сөздер\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"parent.category\",\"en\":\"Parent Category\",\"ru\":\"Родительская категория\",\"kk\":\"Ата-ана санаты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"leaf.category.required\",\"en\":\"Leaf Category Required\",\"ru\":\"Поле \\\"\\\"Листовая категория\\\"\\\" обязательно для заполнения\",\"kk\":\"Парақ санаты өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Парақ санаты\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"server.error\",\"en\":\"Server Error\",\"ru\":\"Ошибка сервера\",\"kk\":\"Сервер қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.query.fail\",\"en\":\"Account Query Failed\",\"ru\":\"Ошибка запроса аккаунта\",\"kk\":\"Аккаунтты сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"empty.request.param\",\"en\":\"Empty query parameter\",\"ru\":\"Пустой параметр запроса\",\"kk\":\"Бос сұрау параметрі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.creation.full.fail\",\"en\":\"Full Account Creation Failed\",\"ru\":\"Ошибка создания полного аккаунта\",\"kk\":\"Толық аккаунтты жасау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.creation.partial.fail\",\"en\":\"Partial Account Creation Failed\",\"ru\":\"Ошибка частичного создания аккаунта\",\"kk\":\"Аккаунтты ішінара жасау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.creation.fail\",\"en\":\"Account Creation Failed\",\"ru\":\"Ошибка создания аккаунта\",\"kk\":\"Аккаунтты жасау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"account.deletion.fail\",\"en\":\"Account Deletion Failed\",\"ru\":\"Ошибка удаления аккаунта\",\"kk\":\"Аккаунтты жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"self.account.deletion.error\",\"en\":\"Self Account Deletion Error\",\"ru\":\"Ошибка удаления собственного аккаунта\",\"kk\":\"Жеке аккаунтты жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"query.fail\",\"en\":\"Query Failed\",\"ru\":\"Ошибка запроса\",\"kk\":\"Сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"audit.confirm.fail\",\"en\":\"Audit Confirmation Failed\",\"ru\":\"Ошибка подтверждения аудита\",\"kk\":\"Аудитті растау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.account\",\"en\":\"Buyer Account\",\"ru\":\"Аккаунт покупателя\",\"kk\":\"Сатып алушының аккаунтты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"supplier.account\",\"en\":\"Supplier Account\",\"ru\":\"Аккаунт поставщика\",\"kk\":\"Жеткізушінің аккаунтты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"supervisor.account\",\"en\":\"Supervisor Account\",\"ru\":\"Аккаунт супервизора\",\"kk\":\"Супервайзер аккаунтты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"expert.account\",\"en\":\"Expert Account\",\"ru\":\"Аккаунт эксперта\",\"kk\":\"Сарапшының аккаунты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"duplicate\",\"en\":\"Duplicate\",\"ru\":\"Дублирование\",\"kk\":\"Қайталау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"yes\",\"en\":\"Yes\",\"ru\":\"Да\",\"kk\":\"Иә\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"no\",\"en\":\"No\",\"ru\":\"Нет\",\"kk\":\"Жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"operation.fail\",\"en\":\"Operation Failed\",\"ru\":\"Операция не выполнена\",\"kk\":\"Операция орындалмады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"required.string.format\",\"en\":\"Required String Format\",\"ru\":\"Строковый формат обязателен\",\"kk\":\"Жол форматы міндетті \",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"content.safety.exception\",\"en\":\"Content Safety Exception\",\"ru\":\"Исключение безопасности контента\",\"kk\":\"Контент қауіпсіздігін жою\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"comment.content.fail\",\"en\":\"Comment Content Fail\",\"ru\":\"Ошибка содержимого комментария\",\"kk\":\"Түсініктеме мазмұнының қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"illegal.char\",\"en\":\"Illegal Character\",\"ru\":\"Недопустимый символ\",\"kk\":\"Жарамсыз таңба\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invalid\",\"en\":\"Invalid\",\"ru\":\"Недействительный\",\"kk\":\"Жарамсыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"valid\",\"en\":\"Valid\",\"ru\":\"Действительный\",\"kk\":\"Жарамды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"to\",\"en\":\"To\",\"ru\":\"До\",\"kk\":\"Дейін\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"pagination.required\",\"en\":\"Pagination Required\",\"ru\":\"Постраничное отображение обязательно\",\"kk\":\"Беттік бейнелеу міндетті\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"interface.error\",\"en\":\"Interface Error\",\"ru\":\"Ошибка интерфейса\",\"kk\":\"Интерфейс қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"supplier.company.name\",\"en\":\"Supplier Company Name\",\"ru\":\"Название компании поставщика\",\"kk\":\"Жеткізуші компанияның атауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"purchaser.company.name\",\"en\":\"Buyer Company Name\",\"ru\":\"Название компании покупателя\",\"kk\":\"Сатып алушы компанияның атауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"total.contract.amount\",\"en\":\"Total Contract Amount\",\"ru\":\"Общая сумма контракта\",\"kk\":\"Келісімшарттың жалпы сомасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"quote.material.list\",\"en\":\"Quote Material List\",\"ru\":\"Список материалов для ценового предложения\",\"kk\":\"Баға ұсынысына арналған материалдар тізімі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"price.validity.period\",\"en\":\"Price Validity Period\",\"ru\":\"Срок действия цены\",\"kk\":\"Бағаның әрекет ету мерзімі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"expected.delivery.date\",\"en\":\"Expected Delivery Date\",\"ru\":\"Ожидаемая дата доставки\",\"kk\":\"Күтілетін жеткізу күні\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"days.after.order\",\"en\":\"Days After Order\",\"ru\":\"Дни после заказа\",\"kk\":\"Тапсырыстан кейінгі күндер\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.deadline\",\"en\":\"Payment Deadline\",\"ru\":\"Срок оплаты\",\"kk\":\"Төлем мерзімі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"payment.channel\",\"en\":\"Payment Channel\",\"ru\":\"Канал оплаты\",\"kk\":\"Төлем арнасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.requirement\",\"en\":\"Invoice Requirement\",\"ru\":\"Требование к счету-фактуре\",\"kk\":\"Шот-фактураға қойылатын талап\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"delivery.address\",\"en\":\"Delivery Address\",\"ru\":\"Адрес доставки\",\"kk\":\"Жеткізу мекенжайы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"specified.tax.rate\",\"en\":\"Specified Tax Rate\",\"ru\":\"Указанная ставка налога\",\"kk\":\"Көрсетілген салық ставкасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"material.name\",\"en\":\"Material Name\",\"ru\":\"Название материала\",\"kk\":\"Материалдың атауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"material.code\",\"en\":\"Material Code\",\"ru\":\"Код материала\",\"kk\":\"Материал коды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"brand\",\"en\":\"Brand\",\"ru\":\"Бренд\",\"kk\":\"Бренд\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"model\",\"en\":\"Model\",\"ru\":\"Модель\",\"kk\":\"Үлгі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"purchase.qty\",\"en\":\"Purchase Quantity\",\"ru\":\"Количество закупки\",\"kk\":\"Сатып алу саны\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"unit\",\"en\":\"Unit\",\"ru\":\"Единица измерения\",\"kk\":\"Өлшем бірлігі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"tax.incl.unit.price\",\"en\":\"Tax-Inclusive Unit Price\",\"ru\":\"Цена за единицу с учетом налога\",\"kk\":\"Салықты ескергендегі бірлік бағасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"tax.rate\",\"en\":\"Tax Rate\",\"ru\":\"Ставка налога\",\"kk\":\"Салық ставкасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"freight\",\"en\":\"Freight\",\"ru\":\"Перевозка\",\"kk\":\"Тасымалдау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"instant.payment\",\"en\":\"Instant Payment\",\"ru\":\"Мгновенная оплата\",\"kk\":\"Жедел төлем\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"monthly.settlement\",\"en\":\"Monthly Settlement\",\"ru\":\"Ежемесячное урегулирование\",\"kk\":\"Ай сайынғы реттеу\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"confirm.receipt.days\",\"en\":\"Confirm receipt of days after settlement\",\"ru\":\"Подтвердить получение дней после урегулирования\",\"kk\":\"Реттеуден кейінгі күндерді алғаныңызды растаңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"stage.payment\",\"en\":\"Stage Payment （Installment payment）\",\"ru\":\"Оплата поэтапно\",\"kk\":\"Кезең-кезеңмен төлеу\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"vat.invoice\",\"en\":\"VAT Invoice\",\"ru\":\"Счет-фактура НДС\",\"kk\":\"ҚҚС шот-фактурасы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"general.taxpayer\",\"en\":\"General tax payer\",\"ru\":\"Плательщик общего налога\",\"kk\":\"Жалпы салық төлеуші\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"no.limited.issuer\",\"en\":\"No Limited Issuer\",\"ru\":\"Без ограничений на эмитента\",\"kk\":\"Эмитентке шектеулер жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"normal.invoice\",\"en\":\"Regular invoice\",\"ru\":\"Обычный счет-фактура\",\"kk\":\"Кәдімгі шот-фактура\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"no.invoice\",\"en\":\"No Invoice\",\"ru\":\"Без счета-фактуры\",\"kk\":\"Шот-фактура жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bank.transfer\",\"en\":\"Bank Transfer\",\"ru\":\"Банковский перевод\",\"kk\":\"Банктік аударым\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bank.electronic.bill\",\"en\":\"Bank Electronic Bill\",\"ru\":\"Электронный банковский чек\",\"kk\":\"Электрондық банктік чек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"pieces\",\"en\":\"Pieces\",\"ru\":\"Штук\",\"kk\":\"Дана\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"clothing\",\"en\":\"Clothing\",\"ru\":\"Одежда\",\"kk\":\"Киім\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"material.required\",\"en\":\"Material field is required\",\"ru\":\"Поле \\\"\\\"Материал\\\"\\\" обязательно для заполнения\",\"kk\":\"Материал өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Материал\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"title.required\",\"en\":\"Title field is required\",\"ru\":\"Поле \\\"\\\"Заголовок\\\"\\\" обязательно для заполнения\",\"kk\":\"Тақырып өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Тақырып\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"invitation.type.required\",\"en\":\"Invitation type field is required\",\"ru\":\"Поле \\\"\\\"Тип приглашения\\\"\\\" обязательно для заполнения\",\"kk\":\"Шақыру түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Шақыру түрі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"contact.address.required\",\"en\":\"Contact address field is required\",\"ru\":\"Поле \\\"\\\"Контактный адрес\\\"\\\" обязательно для заполнения\",\"kk\":\"Байланыс мекенжайы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Байланыс мекенжайы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"purchase.type.required\",\"en\":\"Purchase type field is required\",\"ru\":\"Поле \\\"\\\"Тип закупки\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатып алу түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатып алу түрі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"purchase.requirement.required\",\"en\":\"Purchase requirements field is required\",\"ru\":\"Поле \\\"\\\"Требования к закупке\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатып алуға қойылатын талаптар өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатып алуға қойылатын талаптар\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"quote.time.required\",\"en\":\"Quotation submission time field is required\",\"ru\":\"Поле \\\"\\\"Время подачи ценового предложения\\\"\\\" обязательно для заполнения\",\"kk\":\"Баға ұсынысын беру уақыты өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Баға ұсынысын беру уақыты\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"material.unit.required\",\"en\":\"Material unit field is required\",\"ru\":\"Поле \\\"\\\"Единица материала\\\"\\\" обязательно для заполнения\",\"kk\":\"Материал бірлігі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Материал бірлігі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"material.purchase.qty.required\",\"en\":\"Purchased material quantity field is required\",\"ru\":\"Поле \\\"\\\"Количество закупаемого материала\\\"\\\" обязательно для заполнения\",\"kk\":\"Сатып алынатын материалдың саныі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Сатып алынатын материалдың саны\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"tax.rate.required\",\"en\":\"Tax rate field is required\",\"ru\":\"Поле \\\"\\\"Налоговая ставка\\\"\\\" обязательно для заполнения\",\"kk\":\"Салық ставкасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Салық ставкасы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"publish.time\",\"en\":\"Publication time\",\"ru\":\"Время публикации\",\"kk\":\"Жариялау уақыты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"quote.start\",\"en\":\"Quote Start\",\"ru\":\"Начало подачи ценовых предложений\",\"kk\":\"Баға ұсыныстарын беруді бастау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"quote.end\",\"en\":\"Quote End\",\"ru\":\"Завершение подачи ценовых предложений\",\"kk\":\"Баға ұсыныстарын беруді аяқтау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"enrollment.in.progress\",\"en\":\"Enrollment In Progress\",\"ru\":\"Регистрация в процессе\",\"kk\":\"Процесте тіркеу\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"enrollment.end\",\"en\":\"Enrollment End\",\"ru\":\"Завершение регистрации\",\"kk\":\"Тіркеуді аяқтау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bidding.start\",\"en\":\"Bidding Start\",\"ru\":\"Начало торгов\",\"kk\":\"Сауда-саттықтың басталуы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bidding.end\",\"en\":\"Bidding End\",\"ru\":\"Завершение торгов\",\"kk\":\"Сауда-саттықты аяқтау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.open.start\",\"en\":\"Bid Open Start\",\"ru\":\"Начало рассмотрения заявок\",\"kk\":\"Өтінімдерді қараудың басталуы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.open.end\",\"en\":\"Bid Open End\",\"ru\":\"Завершение рассмотрения заявок\",\"kk\":\"Өтінімдерді қараудың аяқталуы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.evaluation.end\",\"en\":\"Bid Evaluation End\",\"ru\":\"Завершение оценки заявок\",\"kk\":\"Өтінімдерді бағалауды аяқтау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"result.publish\",\"en\":\"Result Publish\",\"ru\":\"Публикация результатов\",\"kk\":\"Нәтижелерді жариялау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"published\",\"en\":\"Published\",\"ru\":\"Опубликовано\",\"kk\":\"Жарияланды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"audit.status.not.empty\",\"en\":\"Audit Status Not Empty\",\"ru\":\"Статус аудита не может быть пустым\",\"kk\":\"Аудит мәртебесі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"publish.success\",\"en\":\"Publish Success\",\"ru\":\"Успешная публикация\",\"kk\":\"Сәтті жариялау\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"screenshot\",\"en\":\"Screenshot\",\"ru\":\"Скриншот\",\"kk\":\"Скриншот\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"param.not.empty\",\"en\":\"Parameter Not Empty\",\"ru\":\"Параметр не может быть пустым\",\"kk\":\"Параметр бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"not.empty\",\"en\":\"Not Empty\",\"ru\":\"Не может быть пустым\",\"kk\":\"Бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invitation.fail\",\"en\":\"Invitation Fail\",\"ru\":\"Ошибка приглашения\",\"kk\":\"Шақыру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"deletion.fail\",\"en\":\"Deletion Fail\",\"ru\":\"Ошибка удаления\",\"kk\":\"Жою қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"experts.number.gt\",\"en\":\"Experts Number Must Be Greater Than or Equal to\",\"ru\":\"Количество экспертов должно быть больше или равно\",\"kk\":\"Сарапшылардың саны көп немесе тең болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"select.award.qty\",\"en\":\"Please Select Award Quantity\",\"ru\":\"Выберите количество наград\",\"kk\":\"Марапаттар санын таңдаңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"award.entry.not.exist\",\"en\":\"Award Entry Does Not Exist\",\"ru\":\"Запись о награде не существует\",\"kk\":\"Сыйақы туралы жазба жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"award.qty.not.less\",\"en\":\"Award Quantity Cannot Be Less Than Quote Quantity\",\"ru\":\"Количество наград не может быть меньше количества ценовых предложений\",\"kk\":\"Марапаттар саны баға ұсыныстарының санынан кем болмауы мүмкін\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"award.qty.gt\",\"en\":\"Award Quantity Exceeds Maximum Quote Quantity\",\"ru\":\"Количество наград превышает максимальное количество ценовых предложений\",\"kk\":\"Марапаттар саны баға ұсыныстарының максималды санынан асады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bidding.start.time.required\",\"en\":\"Bidding Start Time Required\",\"ru\":\"Требуется время начала торгов\",\"kk\":\"Сауда-саттықтың басталу уақыты қажет\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bidding.duration.required\",\"en\":\"Bidding Duration Required\",\"ru\":\"Требуется длительность торгов\",\"kk\":\"Сауда-саттықтың ұзақтығы қажет\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"enrollment.time.required\",\"en\":\"Enrollment Time Required\",\"ru\":\"Требуется время регистрации\",\"kk\":\"Тіркелу уақыты қажет\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"signature.status.error\",\"en\":\"Signature Status Error\",\"ru\":\"Ошибка статуса подписи\",\"kk\":\"Қолтаңба мәртебесінің қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product\",\"en\":\"Product\",\"ru\":\"Продукт\",\"kk\":\"Өнім\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.inquiry\",\"en\":\"Product Inquiry\",\"ru\":\"Запрос продукта\",\"kk\":\"Өнім сұрауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"quote.sheet\",\"en\":\"Quote Sheet\",\"ru\":\"Лист ценовых предложений\",\"kk\":\"Баға ұсыныстарының парағы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"not.exist\",\"en\":\"Does Not Exist\",\"ru\":\"Не существует\",\"kk\":\"Жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"pr.req.update.fail\",\"en\":\"Purchase Request Update Failed\",\"ru\":\"Ошибка обновления заявки на закупку\",\"kk\":\"Сатып алу өтінімін жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.sheet.update.fail\",\"en\":\"Bid Sheet Update Failed\",\"ru\":\"Ошибка обновления листа заявок\",\"kk\":\"Өтінім парағын жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.company.name\",\"en\":\"Buyer Company Name\",\"ru\":\"Название компании покупателя\",\"kk\":\"Сатып алушы компаниясының атауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller.company.name\",\"en\":\"Seller Company Name\",\"ru\":\"Название компании продавца\",\"kk\":\"Сатушы компаниясының атауы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"buyer.no.company.info\",\"en\":\"Buyer No Company Info\",\"ru\":\"У покупателя нет информации о компании\",\"kk\":\"Сатып алушыда компания туралы ақпарат жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.edit\",\"en\":\"Cannot Edit\",\"ru\":\"Редактирование невозможно\",\"kk\":\"Өңдеу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"contract.number.required\",\"en\":\"Contract Number Required\",\"ru\":\"Поле \\\"\\\"Номер контракта\\\"\\\" обязательно для заполнения\",\"kk\":\"Келісімшарт нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Келісімшарт нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"contract.name.required\",\"en\":\"Contract Name Required\",\"ru\":\"Поле \\\"\\\"Название контракта\\\"\\\" обязательно для заполнения\",\"kk\":\"Келісімшарт атауы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Келісімшарт атауы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"signature.method.required\",\"en\":\"Signature Method Required\",\"ru\":\"Поле \\\"\\\"Метод подписи\\\"\\\" обязательно для заполнения\",\"kk\":\"Қол қою әдісі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қол қою әдісі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"contract.type.required\",\"en\":\"Contract Type Required\",\"ru\":\"Поле \\\"\\\"Тип контракта\\\"\\\" обязательно для заполнения\",\"kk\":\"Келісімшарт түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Келісімшарт түрі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"signature.order.required\",\"en\":\"Signature Order Required\",\"ru\":\"Поле \\\"\\\"Порядок подписи\\\"\\\" обязательно для заполнения\",\"kk\":\"Қол қою тәртібі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қол қою тәртібі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"expected.effective.time.required\",\"en\":\"Expected Effective Time Required\",\"ru\":\"Поле \\\"\\\"Ожидаемое время вступления в силу\\\"\\\" обязательно для заполнения\",\"kk\":\"Күтілетін күшіне ену уақыты өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Күтілетін күшіне ену уақыты\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"expected.effective.time.invalid\",\"en\":\"Expected Effective Time Invalid\",\"ru\":\"Неверное ожидаемое время вступления в силу\",\"kk\":\"Күшіне енудің қате күтілетін уақыты\",\"ru_new\":\"\",\"kk new\":\"Күтелетін күшіне ену уақыты қате\"}\n" );
        sb.append( "{\"key\":\"contract.document.required\",\"en\":\"Contract Document Required\",\"ru\":\"Поле \\\"\\\"Документ контракта\\\"\\\" обязательно для заполнения\",\"kk\":\"Келісімшарт құжаты өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Келісімшарт құжаты\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"ss.req.update.fail\",\"en\":\"Sourcing Sheet Update Failed\",\"ru\":\"Ошибка обновления листа закупок\",\"kk\":\"Сатып алу парағын жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.time\",\"en\":\"Bid Time\",\"ru\":\"Время подачи заявки\",\"kk\":\"Өтінім беру уақыты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.open.time\",\"en\":\"Bid Open Time\",\"ru\":\"Время рассмотрения заявок\",\"kk\":\"Өтінімдерді қарау уақыты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bid.evaluation.time\",\"en\":\"Bid Evaluation Time\",\"ru\":\"Время оценки заявок\",\"kk\":\"Өтінімдерді бағалау уақыты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"system.exception\",\"en\":\"System Exception\",\"ru\":\"Ошибка системы\",\"kk\":\"Жүйе қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"no.permission\",\"en\":\"No Permission\",\"ru\":\"Нет прав доступа\",\"kk\":\"Кіру құқығы жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"param.error\",\"en\":\"Parameter Error\",\"ru\":\"Ошибка параметра\",\"kk\":\"Параметр қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"plz.try.again.later\",\"en\":\"Try Again Later\",\"ru\":\"Попробуйте позже\",\"kk\":\"Кейінірек көріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"data.inaccessible\",\"en\":\"Data Inaccessible\",\"ru\":\"Данные недоступны\",\"kk\":\"Деректер қол жетімді емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"data.not.exist\",\"en\":\"Data Does Not Exist\",\"ru\":\"Данные не существуют\",\"kk\":\"Деректер жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.not.orderable\",\"en\":\"Product Not Orderable\",\"ru\":\"Продукт недоступен для заказа\",\"kk\":\"Өнім тапсырыс беру үшін қол жетімді емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"purchase.other.product\",\"en\":\"Purchase Other Product\",\"ru\":\"Приобретите другой продукт\",\"kk\":\"Басқа өнімді сатып алыңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"e.voucher.expired\",\"en\":\"E-Voucher Expired\",\"ru\":\"Электронный ваучер истёк\",\"kk\":\"Электрондық ваучердің мерзімі аяқталды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cart.qty.limit\",\"en\":\"Cart Quantity Limit Reached\",\"ru\":\"Достигнут лимит количества в корзине\",\"kk\":\"Себеттегі сан лимитіне қол жеткізілді\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"product.temporarily.unorderable\",\"en\":\"Product Temporarily Unorderable\",\"ru\":\"Продукт временно недоступен для заказа\",\"kk\":\"Өнім тапсырыс беру үшін уақытша қол жетімді емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"contact.seller\",\"en\":\"Contact Seller\",\"ru\":\"Свяжитесь с продавцом\",\"kk\":\"Сатушыға хабарласыңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bs.merchant\",\"en\":\"BS Merchant\",\"ru\":\"Продавец BS\",\"kk\":\"BS сатушысы\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bs.member\",\"en\":\"BS Member\",\"ru\":\"Участник BS\",\"kk\":\"BS мүшесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"bs.temp.member\",\"en\":\"BS Temporary Member\",\"ru\":\"Временный участник BS\",\"kk\":\"BS уақытша мүшесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"username.required\",\"en\":\"Username Required\",\"ru\":\"Поле \\\"\\\"Имя пользователя\\\"\\\" обязательно для заполнения\",\"kk\":\"Пайдаланушының аты өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Пайдаланушының аты\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"phone.required\",\"en\":\"Phone Number Required\",\"ru\":\"Поле \\\"\\\"Номер телефона\\\"\\\" обязательно для заполнения\",\"kk\":\"Телефон нөмірі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Телефон нөмірі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"password.required\",\"en\":\"Password Required\",\"ru\":\"Поле \\\"\\\"Пароль\\\"\\\" обязательно для заполнения\",\"kk\":\"Құпия сөз өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Құпия сөз өрісі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"username.length.range\",\"en\":\"Username Length Range\",\"ru\":\"Диапазон длины имени пользователя\",\"kk\":\"Пайдаланушы аты ұзындығының диапазоны\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"password.length.range\",\"en\":\"Password Length Range\",\"ru\":\"Диапазон длины пароля\",\"kk\":\"Құпия сөз ұзындығының диапазоны\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"phone.captcha.required\",\"en\":\"Phone Captcha Required\",\"ru\":\"Поле \\\"\\\"Телефонная капча\\\"\\\" обязательно для заполнения\",\"kk\":\"Телефон капчасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Телефон капчасы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"new.password.required\",\"en\":\"New Password Required\",\"ru\":\"Поле \\\"\\\"Новый пароль\\\"\\\" обязательно для заполнения\",\"kk\":\"Жаңа құпия сөз өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Жаңа құпия сөз\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"password.min.length\",\"en\":\"Password Must Be At Least Characters and Include Letters and Numbers\",\"ru\":\"Пароль должен быть не менее [числа] символов и содержать буквы и цифры\",\"kk\":\"Құпия сөз [сандар] таңбадан кем болмауы керек және әріптер мен сандарды қамтуы тиіс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"phone.format.invalid\",\"en\":\"Phone Number Format Invalid\",\"ru\":\"Неверный формат номера телефона\",\"kk\":\"Телефон нөмірінің форматы қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"logout.reason.required\",\"en\":\"Logout Reason Required\",\"ru\":\"Поле \\\"\\\"Причина выхода\\\"\\\" обязательно для заполнения\",\"kk\":\"Шығу себебі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Шығу себеб\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"type.required\",\"en\":\"Type Required\",\"ru\":\"Поле \\\"\\\"Тип\\\"\\\" обязательно для заполнения\",\"kk\":\"Түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Түрі\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"user.info.not.exist\",\"en\":\"User Information Does Not Exist\",\"ru\":\"Информация о пользователе не существует\",\"kk\":\"Пайдаланушы туралы ақпарат жоқ\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"try.again\",\"en\":\"Try Again\",\"ru\":\"Попробуйте снова\",\"kk\":\"Қайталап көріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"login.fail\",\"en\":\"Login Failed\",\"ru\":\"Ошибка входа\",\"kk\":\"Кіру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.creation.fail\",\"en\":\"User Creation Failed\",\"ru\":\"Ошибка создания пользователя\",\"kk\":\"Пайдаланушыны құру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"password.verification.fail\",\"en\":\"Password Verification Failed\",\"ru\":\"Ошибка проверки пароля\",\"kk\":\"Құпия сөзді тексеру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.phone.binding.fail\",\"en\":\"User Phone Binding Failed\",\"ru\":\"Ошибка привязки телефона пользователя\",\"kk\":\"Пайдаланушының телефонын байланыстыру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.update.fail\",\"en\":\"User Update Failed\",\"ru\":\"Ошибка обновления пользователя\",\"kk\":\"Пайдаланушыны жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.query.fail\",\"en\":\"User Query Failed\",\"ru\":\"Ошибка запроса пользователя\",\"kk\":\"Пайдаланушы сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"profile.uneditable\",\"en\":\"Profile Uneditable\",\"ru\":\"Профиль не редактируем\",\"kk\":\"Профильді өңдеу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"reset.password.fail\",\"en\":\"Reset Password Failed\",\"ru\":\"Ошибка сброса пароля\",\"kk\":\"Құпия сөзді қалпына келтіру қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"phone.invalid\",\"en\":\"The Phone Number You Entered is Incorrect\",\"ru\":\"Введенный номер телефона неверен\",\"kk\":\"Енгізілген телефон нөмірі қате\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"verify.and.retry\",\"en\":\"Please Verify and Re-enter\",\"ru\":\"Пожалуйста, проверьте и введите снова\",\"kk\":\"Тексеруіңізді және қайта енгізуіңізді өтінеміз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"user.exist\",\"en\":\"User Exists\",\"ru\":\"Пользователь существует\",\"kk\":\"Пайдаланушы бар\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"cannot.re.register\",\"en\":\"Cannot Re-register\",\"ru\":\"Повторная регистрация невозможна\",\"kk\":\"Қайта тіркеу мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"shortlink.creation.fail\",\"en\":\"Shortlink Creation Failed\",\"ru\":\"Ошибка создания короткой ссылки\",\"kk\":\"Қысқа сілтемені жасау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"contact.admin\",\"en\":\"Contact Admin\",\"ru\":\"Свяжитесь с администратором\",\"kk\":\"Әкімшіге хабарласыңыз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"shortlink.fetch.fail\",\"en\":\"Shortlink Fetch Fail\",\"ru\":\"Ошибка получения короткой ссылки\",\"kk\":\"Қысқа сілтемені алу қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"source.link.empty\",\"en\":\"Source Link Empty\",\"ru\":\"Пустая исходная ссылка\",\"kk\":\"Бос бастапқы сілтеме\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"password.format\",\"en\":\"At Least Characters and Include Numbers and Letters\",\"ru\":\"Должно быть хотя бы 8 символов, включая цифры и буквы\",\"kk\":\"Сандар мен әріптерді қосқанда кем дегенде 8 таңба болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"system.busy\",\"en\":\"System Busy\",\"ru\":\"Система занята, попробуйте позже\",\"kk\":\"Жүйе бос емес, кейінірек көріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"try.again.later\",\"en\":\"Try Again Later\",\"ru\":\"Попробуйте еще раз позже\",\"kk\":\"Кейінірек қайталап көріңіз\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.judgement.query.fail\",\"en\":\"Points Judgement Query Failed\",\"ru\":\"Ошибка запроса оценки баллов\",\"kk\":\"Ұпайларды бағалауды сұрау қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"new.user.registration\",\"en\":\"New User Registration\",\"ru\":\"Регистрация нового пользователя\",\"kk\":\"Жаңа пайдаланушыны тіркеу\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"auth.autologin.points\",\"en\":\"Auth Autologin Points\",\"ru\":\"Авторизация и автологин с баллами\",\"kk\":\"Ұпайлары бар авторизация және автологин\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"shortlink.source\",\"en\":\"Shortlink Source\",\"ru\":\"Источник короткой ссылки\",\"kk\":\"Қысқа сілтеме көзі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"exception\",\"en\":\"Exception\",\"ru\":\"Исключение\",\"kk\":\"Ерекшелік\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"qty.required\",\"en\":\"Quantity Required\",\"ru\":\"Поле \\\"\\\"Количество\\\"\\\" обязательно для заполнения\",\"kk\":\"Саны өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Саны\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"invoice\",\"en\":\"Invoice\",\"ru\":\"Налоговая накладная\",\"kk\":\"Салық жүкқұжаты\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"invoice.type.required\",\"en\":\"Invoice Type Required\",\"ru\":\"Поле \\\"\\\"Тип накладной\\\"\\\" обязательно для заполнения\",\"kk\":\"Жүкқұжат түрі өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Жүкқұжат түрі\\\"\\\" өрісін міндетті түрде толтыру қажет \"}\n" +
                "{\"key\":\"invoice.title.required\",\"en\":\"Invoice Title Required\",\"ru\":\"Поле \\\"\\\"Заголовок накладной\\\"\\\" обязательно для заполнения\",\"kk\":\"Жүкқұжат тақырыбы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Жүкқұжат тақырыбы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"invoice.tax.code.required\",\"en\":\"Invoice Tax Code Required\",\"ru\":\"Поле \\\"\\\"Налоговый код накладной\\\"\\\" обязательно для заполнения\",\"kk\":\"Жүкқұжаттың салық коды өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Жүкқұжаттың салық\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"sub.order\",\"en\":\"Sub Order\",\"ru\":\"Подзаказ\",\"kk\":\"Қосалқы тапсырыс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" );
        sb.append( "{\"key\":\"image.url.invalid\",\"en\":\"Image URL Invalid\",\"ru\":\"Неверный URL изображения\",\"kk\":\"Сурет URL қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"video.url.invalid\",\"en\":\"Video URL Invalid\",\"ru\":\"Неверный URL видео\",\"kk\":\"Бейне URL қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"rating.required\",\"en\":\"Rating Required\",\"ru\":\"Поле \\\"\\\"Рейтинг\\\"\\\" обязательно для заполнения\",\"kk\":\"Рейтинг өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Рейтинг\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"rating.range\",\"en\":\"Rating Range\",\"ru\":\"Диапазон рейтинга\",\"kk\":\"Рейтинг диапазоны\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"refund.amount.required\",\"en\":\"Refund Amount Required\",\"ru\":\"Поле \\\"\\\"Сумма возврата\\\"\\\" обязательно для заполнения\",\"kk\":\"Қайтару сомасы өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қайтару сомасы\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"refund.min.amount\",\"en\":\"Refund Amount At Least Cents\",\"ru\":\"Сумма возврата не менее 1 цента\",\"kk\":\"Қайтару сомасы 1 центтен кем емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"return.qty.required\",\"en\":\"Return Quantity Required\",\"ru\":\"Поле \\\"\\\"Количество для возврата\\\"\\\" обязательно для заполнения\",\"kk\":\"Қайтару саны өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Қайтару саны\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"return.min.qty\",\"en\":\"Return Quantity At Least One\",\"ru\":\"Требуется хотя бы 1 единица для возврата\",\"kk\":\"Қайтару үшін кем дегенде 1 бірлік қажет\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"casKey.required\",\"en\":\"Cas Required\",\"ru\":\"Поле \\\"\\\"CAS\\\"\\\" обязательно для заполнения\",\"kk\":\"CAS өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"CAS\\\"\\\" өрісін міндетті түрде толтыру қажет\"}\n" +
                "{\"key\":\"iin.empty\",\"en\":\"iin Is Empty\",\"ru\":\"Поле \\\"\\\"ИИН\\\"\\\" не заполнено\",\"kk\":\"ЖСН жолағы толтырылмаған\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"ЖСН\\\"\\\" өрісі толтырылмаған\"}\n" +
                "{\"key\":\"login.type.illegal\",\"en\":\"Unsupported Login Type\",\"ru\":\"Неподдерживаемый тип входа\",\"kk\":\"Қолдау көрсетілмейтін кіру түрі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"favourite.exceed.limit\",\"en\":\"Customer Favourite Exceeds Limit\",\"ru\":\"Превышен лимит избранных товаров\",\"kk\":\"Таңдалған тауарлар лимитінен асып кетті\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"update.language.fail\",\"en\":\"Customer Update language fail\",\"ru\":\"Ошибка обновления языка клиента\",\"kk\":\"Клиент тілін жаңарту қатесі\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"app.required\",\"en\":\"App cannot be empty\",\"ru\":\"Приложение не может быть пустым\",\"kk\":\"Қосымша бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"id.required\",\"en\":\"ID cannot be empty\",\"ru\":\"Идентификатор не может быть пустым\",\"kk\":\"Идентификатор бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"name.required\",\"en\":\"Name cannot be empty\",\"ru\":\"Имя не может быть пустым\",\"kk\":\"Аты бос болуы мүмкін емес\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"price.required\",\"en\":\"Price cannot be empty\",\"ru\":\"Цена не может быть пустой\",\"kk\":\"Бағасы бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"price.min\",\"en\":\"Price cannot be less than 1\",\"ru\":\"Цена не может быть меньше 1\",\"kk\":\"Бағасы 1-ден кем бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"price.max\",\"en\":\"Price cannot exceed 10\",\"ru\":\"Цена не может превышать 10\",\"kk\":\"Бағасы 10 -нан аспауы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"coupon.code.required\",\"en\":\"Coupon CODE cannot be empty\",\"ru\":\"Поле \\\"\\\"Код купона\\\"\\\" не может быть пустым\",\"kk\":\"Купон коды өрісі бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"Купон коды\\\"\\\" өрісі бос бола алмайды\"}\n" +
                "{\"key\":\"coupon.map.required\",\"en\":\"Coupon Map<Id,Code> cannot be empty\",\"ru\":\"Карта купонов (Идентификатор, Code) не может быть пустой\",\"kk\":\"Купон картасы (Идентификатор, Code) бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller.id.not.null\",\"en\":\"Seller ID cannot be null\",\"ru\":\"Идентификатор продавца не может быть пустым\",\"kk\":\"Сатушы идентификаторы бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"seller.id.required\",\"en\":\"Seller ID cannot be empty\",\"ru\":\"Идентификатор продавца не может быть пустым\",\"kk\":\"Сатушы идентификаторы бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"idempotent.id.required\",\"en\":\"Idempotent ID cannot be empty\",\"ru\":\"Идентификатор идемпотентности не может быть пустым\",\"kk\":\"Идемпотенттік идентификатор бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"new.product.count.min\",\"en\":\"New product count must be greater than 0\",\"ru\":\"Количество новых товаров должно быть больше 0\",\"kk\":\"Жаңа тауарлардың саны 0-ден көп болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"points.coupon.is.benefit\",\"en\":\"Points coupons are all benefit coupons\",\"ru\":\"Балловые купоны являются купонами для получения выгоды\",\"kk\":\"Ұпайлық купондар пайда алуға арналған купондар болып табылады\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"recipient.name.length\",\"en\":\"Recipient name must not exceed 25 characters\",\"ru\":\"Имя получателя не должно превышать 25 символов\",\"kk\":\"Алушының аты 25 таңбадан аспауы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"refund.amount.min\",\"en\":\"Refund amount must be at least 1 cent\",\"ru\":\"Сумма возврата должна быть не менее 1 цента\",\"kk\":\"Қайтару сомасы кемінде 1 цент болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"rating.score.range\",\"en\":\"Rating score must be between 1 and 5\",\"ru\":\"Оценка должна быть от 1 до 5\",\"kk\":\"Бағалау 1-ден 5-ке дейін болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"stage.number.positive\",\"en\":\"Stage number must be a positive integer\",\"ru\":\"Номер этапа должен быть положительным целым числом\",\"kk\":\"Кезең нөмірі оң бүтін сан болуы керек\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"membership.b.c.required\",\"en\":\"Membership in either B or C must not be empty\",\"ru\":\"Членство в категориях B или C не должно быть пустым\",\"kk\":\"B немесе C санаттарына мүшелік бос бола алмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"membership.b.c.mutual.exclusive\",\"en\":\"Cannot join both B and C memberships simultaneously\",\"ru\":\"Нельзя одновременно вступить в оба членства (B и C)\",\"kk\":\"Екі мүшелікке (B және C)бір уақытта кіруге болмайды\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"password.requirements\",\"en\":\"Password must be at least 8 characters and include letters and numbers\",\"ru\":\"Пароль должен содержать хотя бы 8 символов и включать буквы и цифры\",\"kk\":\"Құпия сөз кем дегенде 8 таңбадан тұруы керек және әріптер мен сандарды қамтуы тиіс\",\"ru_new\":\"\",\"kk new\":\"\"}\n" +
                "{\"key\":\"iin.required\",\"en\":\"IIN Required\",\"ru\":\"Поле \\\"\\\"ИИН\\\"\\\" обязательно для заполнения\",\"kk\":\"ЖСН өрісі міндетті түрде толтырылады\",\"ru_new\":\"\",\"kk new\":\"\\\"\\\"ЖСН\\\"\\\" өрісін міндетті түрде толтыру қажет\"}]");
        String jsonArray = sb.toString();
        JSONArray jsonObj = JSON.parseArray(jsonArray);
        List<Map<String, String>> langList = new ArrayList<>();
//        Map<String, String> en = new HashMap<>();
//        en.put("lang", "en");
//        en.put("path", "messages_mall_front_en.properties");
//        langList.add(en);

//        Map<String, String> kk = new HashMap<>();
//        kk.put("lang", "kk");
//        kk.put("path", "messages_mall_front_kk.properties");
//        langList.add(kk);
//
        Map<String, String> ru = new HashMap<>();
        ru.put("lang", "ru");
        ru.put("path", "messages_mall_front_ru.properties");
        langList.add(ru);
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
                        if (StringUtils.isNotEmpty(j.getString("ru_new"))) {
                            System.out.println(String.valueOf(key) + "=" + j.getString("ru_new"));
                            exist = Boolean.TRUE;
                            break;
                        }
//                        if (StringUtils.isNotEmpty(j.getString("kk new"))) {
//                            System.out.println(String.valueOf(key) + "=" + j.getString("kk new"));
//                            exist = Boolean.TRUE;
//                            break;
//                        }
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
