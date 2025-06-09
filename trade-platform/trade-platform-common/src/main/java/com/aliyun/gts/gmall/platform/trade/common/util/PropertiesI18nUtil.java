package com.aliyun.gts.gmall.platform.trade.common.util;

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

        String jsonArray = "[{\"key\":\"seller\",\"en\":\"Seller\",\"ru\":\"Продавец\",\"kk\":\"Сатушы\"}\n" +
                "{\"key\":\"order.error\",\"en\":\"Order Number Error\",\"ru\":\"Ошибка номера заказа\",\"kk\":\"Тапсырыс нөмірінің қатесі\"}\n" +
                "{\"key\":\"platform.account\",\"en\":\"Platform Revenue Account\",\"ru\":\"Платежный аккаунт\",\"kk\":\"Төлем аккаунты\"}\n" +
                "{\"key\":\"merchant.account\",\"en\":\"Merchant Account\",\"ru\":\"Торговый аккаунт\",\"kk\":\"Сауда аккаунты\"}\n" +
                "{\"key\":\"pending.approval\",\"en\":\"Pending Approval\",\"ru\":\"Ожидает утверждения\",\"kk\":\"Бекітуді күтуде\"}\n" +
                "{\"key\":\"in.approval\",\"en\":\"In Approval\",\"ru\":\"На утверждении\",\"kk\":\"Бекітуде\"}\n" +
                "{\"key\":\"approved\",\"en\":\"Approved\",\"ru\":\"Одобрено\",\"kk\":\"Мақұлданды\"}\n" +
                "{\"key\":\"rejected\",\"en\":\"Rejected\",\"ru\":\"Отклонено\",\"kk\":\"Қабылданбады\"}\n" +
                "{\"key\":\"seller.group\",\"en\":\"Seller Group\",\"ru\":\"Группа продавцов\",\"kk\":\"Сатушылар тобы\"}\n" +
                "{\"key\":\"expired.group\",\"en\":\"Expired Products Group\",\"ru\":\"Группа просроченных товаров\",\"kk\":\"Мерзімі өткен тауарлар тобы\"}\n" +
                "{\"key\":\"initialization\",\"en\":\"Initialization\",\"ru\":\"Инициализация\",\"kk\":\"Инициализация\"}\n" +
                "{\"key\":\"processing\",\"en\":\"Processing\",\"ru\":\"Обработка\",\"kk\":\"Өңдеу\"}\n" +
                "{\"key\":\"success\",\"en\":\"Success\",\"ru\":\"Успешно\",\"kk\":\"Сәтті\"}\n" +
                "{\"key\":\"failure\",\"en\":\"Failure\",\"ru\":\"Не удалось\",\"kk\":\"Сәтсіз аяқталды\"}\n" +
                "{\"key\":\"normal.trade\",\"en\":\"Normal Trade\",\"ru\":\"Обычная сделка\",\"kk\":\"Кәдімгі мәміле\"}\n" +
                "{\"key\":\"normal.activity\",\"en\":\"Normal Activity\",\"ru\":\"Обычная активность\",\"kk\":\"Кәдімгі белсенділік\"}\n" +
                "{\"key\":\"non.asset\",\"en\":\"Non-Asset\",\"ru\":\"Неактив\",\"kk\":\"Белсенді емес\"}\n" +
                "{\"key\":\"asset.activity\",\"en\":\"Asset Activity\",\"ru\":\"Активность с активами\",\"kk\":\"Активтермен белсенділік\"}\n" +
                "{\"key\":\"none\",\"en\":\"None\",\"ru\":\"Нет\",\"kk\":\"Жоқ\"}\n" +
                "{\"key\":\"coupon\",\"en\":\"Coupon\",\"ru\":\"Купон\",\"kk\":\"Купон\"}\n" +
                "{\"key\":\"express\",\"en\":\"Express\",\"ru\":\"Экспресс-доставка\",\"kk\":\"Жедел жеткізу\"}\n" +
                "{\"key\":\"no.logistics\",\"en\":\"No Logistics\",\"ru\":\"Без логистики\",\"kk\":\"Логистика жоқ\"}\n" +
                "{\"key\":\"self.pickup\",\"en\":\"Self Pickup\",\"ru\":\"Самовывоз\",\"kk\":\"Алып кету\"}\n" +
                "{\"key\":\"pending.agree\",\"en\":\"Pending Seller Agreement\",\"ru\":\"Ожидает согласования с продавцом\",\"kk\":\"Сатушымен келісуді күтеді\"}\n" +
                "{\"key\":\"pending.return\",\"en\":\"Pending Return Shipment\",\"ru\":\"Ожидает отправки возврата\",\"kk\":\"Қайтаруды күтеді\"}\n" +
                "{\"key\":\"pending.receipt\",\"en\":\"Pending Receipt Confirmation\",\"ru\":\"Ожидает подтверждения получения\",\"kk\":\"Алуды растауды күтеді\"}\n" +
                "{\"key\":\"refunding\",\"en\":\"Refunding\",\"ru\":\"Возврат средств в процессе\",\"kk\":\"Қаражатты қайтару процесте\"}\n" +
                "{\"key\":\"aftersale.complete\",\"en\":\"Aftersale Complete\",\"ru\":\"Завершение послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі қызмет көрсетуді аяқтау\"}\n" +
                "{\"key\":\"buyer.cancel\",\"en\":\"Buyer Cancelled\",\"ru\":\"Покупатель отменил\",\"kk\":\"Сатып алушы бас тартты\"}\n" +
                "{\"key\":\"seller.reject\",\"en\":\"Seller Rejected\",\"ru\":\"Продавец отклонил\",\"kk\":\"Сатушы қабылдамады\"}\n" +
                "{\"key\":\"waiting.payment\",\"en\":\"Waiting for Payment\",\"ru\":\"Ожидает оплаты\",\"kk\":\"Төлемді күтуде\"}\n" +
                "{\"key\":\"pending.shipment\",\"en\":\"Pending Shipment\",\"ru\":\"Ожидает отправки\",\"kk\":\"Жөнелтуді күтуде\"}\n" +
                "{\"key\":\"shipped\",\"en\":\"Shipped\",\"ru\":\"Отправлено\",\"kk\":\"Жіберілді\"}\n" +
                "{\"key\":\"multi.stage\",\"en\":\"Multi-Stage in Progress\",\"ru\":\"Многоступенчатая обработка\",\"kk\":\"Көп сатылы өңдеу\"}\n" +
                "{\"key\":\"confirm.receipt\",\"en\":\"Confirm Receipt\",\"ru\":\"Подтверждение получения\",\"kk\":\"Қабылдауды растау\"}\n" +
                "{\"key\":\"system.receipt\",\"en\":\"System Receipt Confirmation\",\"ru\":\"Системное подтверждение получения\",\"kk\":\"Қабылдауды жүйелік растау\"}\n" +
                "{\"key\":\"seller.close\",\"en\":\"Seller Closed\",\"ru\":\"Продавец закрыт\",\"kk\":\"Сатушы жабық\"}\n" +
                "{\"key\":\"system.close\",\"en\":\"System Closed\",\"ru\":\"Система закрыта\",\"kk\":\"Жүйе жабық\"}\n" +
                "{\"key\":\"aftersale.process\",\"en\":\"Aftersale In Progress\",\"ru\":\"Послепродажное обслуживание в процессе\",\"kk\":\"Сатудан кейінгі қызмет көрсету процесте\"}\n" +
                "{\"key\":\"waiting.confirm\",\"en\":\"Waiting for Seller to Confirm Order\",\"ru\":\"Ожидает подтверждения заказа продавцом\",\"kk\":\"Сатушының тапсырысты растауын күтеді\"}\n" +
                "{\"key\":\"waiting.pay.confirm\",\"en\":\"Waiting for Seller to Confirm Payment\",\"ru\":\"Ожидает подтверждения оплаты продавцом\",\"kk\":\"Сатушының төлемді растауын күтеді\"}\n" +
                "{\"key\":\"waiting.buyer.confirm\",\"en\":\"Waiting for Buyer to Confirm Payment\",\"ru\":\"Ожидает подтверждения оплаты покупателем\",\"kk\":\"Сатып алушының төлемді растауын күтеді \"}\n" +
                "{\"key\":\"not.started\",\"en\":\"Not Started\",\"ru\":\"Не начато\",\"kk\":\"Басталмады\"}\n" +
                "{\"key\":\"pending.payment\",\"en\":\"Pending Payment\",\"ru\":\"Ожидает оплаты\",\"kk\":\"Төлемді күтуде\"}\n" +
                "{\"key\":\"pending.merchant\",\"en\":\"Pending Merchant Processing\",\"ru\":\"Ожидает обработки продавцом\",\"kk\":\"Сатушының өңдеуін күтеді\"}\n" +
                "{\"key\":\"pending.user\",\"en\":\"Pending User Confirmation\",\"ru\":\"Ожидает подтверждения пользователем\",\"kk\":\"Пайдаланушы растауын күтеді\"}\n" +
                "{\"key\":\"complete\",\"en\":\"Complete\",\"ru\":\"Завершено\",\"kk\":\"Аяқталды\"}\n" +
                "{\"key\":\"not.reviewed\",\"en\":\"Not Reviewed\",\"ru\":\"Не оценено\",\"kk\":\"Бағаланбаған\"}\n" +
                "{\"key\":\"reviewed\",\"en\":\"Reviewed\",\"ru\":\"Оценено\",\"kk\":\"Бағаланды\"}\n" +
                "{\"key\":\"reviewed.added\",\"en\":\"Additional Review\",\"ru\":\"Дополнительная проверка\",\"kk\":\"Қосымша тексеру\"}\n" +
                "{\"key\":\"normal.order\",\"en\":\"Normal Physical Order\",\"ru\":\"Обычный физический заказ\",\"kk\":\"Кәдімгі физикалық тапсырыс\"}\n" +
                "{\"key\":\"multi.stage.order\",\"en\":\"Multi-Stage Order\",\"ru\":\"Многоступенчатый заказ\",\"kk\":\"Көп сатылы тапсырыс\"}\n" +
                "{\"key\":\"page\",\"en\":\"Page\",\"ru\":\"Страница\",\"kk\":\"Бет\"}\n" +
                "{\"key\":\"backend\",\"en\":\"Seller Backend\",\"ru\":\"Панель продавца\",\"kk\":\"Сатушы панелі\"}\n" +
                "{\"key\":\"refund.only\",\"en\":\"Refund Only\",\"ru\":\"Только возврат\",\"kk\":\"Тек қайтару\"}\n" +
                "{\"key\":\"return.refund\",\"en\":\"Return and Refund\",\"ru\":\"Возврат и возврат средств\",\"kk\":\"Қайтару және қаражатты қайтару\"}\n" +
                "{\"key\":\"listed\",\"en\":\"Listed\",\"ru\":\"В листинге\",\"kk\":\"Листингте\"}\n" +
                "{\"key\":\"delisted\",\"en\":\"Delisted\",\"ru\":\"Удален из листинга\",\"kk\":\"Листингтен жойылды\"}\n" +
                "{\"key\":\"auto.refund\",\"en\":\"Auto Refund for Overselling\",\"ru\":\"Автоматический возврат при перепродаже\",\"kk\":\"Қайта сату кезінде автоматты түрде қайтару\"}\n" +
                "{\"key\":\"system.refund\",\"en\":\"System Auto Refund\",\"ru\":\"Автоматический возврат системы\",\"kk\":\"Жүйені автоматты түрде қайтару\"}\n" +
                "{\"key\":\"main.order\",\"en\":\"Main Order\",\"ru\":\"Основной заказ\",\"kk\":\"Негізгі тапсырыс\"}\n" +
                "{\"key\":\"sub.order\",\"en\":\"Sub Order\",\"ru\":\"Подзаказ\",\"kk\":\"Қосалқы тапсырыс\"}\n" +
                "{\"key\":\"sf.express\",\"en\":\"SF Express\",\"ru\":\"SF Express\",\"kk\":\"SF Express\"}\n" +
                "{\"key\":\"best.express\",\"en\":\"Best Express\",\"ru\":\"Best Express\",\"kk\":\"Best Express\"}\n" +
                "{\"key\":\"enable\",\"en\":\"Enable\",\"ru\":\"Включить\",\"kk\":\"Қосу\"}\n" +
                "{\"key\":\"block\",\"en\":\"Block\",\"ru\":\"Блокировать\",\"kk\":\"Бұғаттау\"}\n" +
                "{\"key\":\"auto.close\",\"en\":\"Auto Close Order\",\"ru\":\"Автоматическое закрытие заказа\",\"kk\":\"Тапсырысты автоматты түрде жабу\"}\n" +
                "{\"key\":\"manual.process\",\"en\":\"Manual Processing\",\"ru\":\"Ручная обработка\",\"kk\":\"Қолмен өңдеу\"}\n" +
                "{\"key\":\"normal\",\"en\":\"Normal\",\"ru\":\"Нормально\",\"kk\":\"Қалыпты\"}\n" +
                "{\"key\":\"removed\",\"en\":\"Removed\",\"ru\":\"Удалено\",\"kk\":\"Жойылды\"}\n" +
                "{\"key\":\"order.deduct\",\"en\":\"Order Deduct Inventory\",\"ru\":\"Заказ списывает товар\",\"kk\":\"Тапсырыс тауарды есептен шығарады\"}\n" +
                "{\"key\":\"payment.deduct\",\"en\":\"Payment Deduct Inventory\",\"ru\":\"Платеж списывает товар\",\"kk\":\"Төлем тауарды есептен шығарады\"}\n" +
                "{\"key\":\"create.order\",\"en\":\"Create Order\",\"ru\":\"Создать заказ\",\"kk\":\"Тапсырыс жасау\"}\n" +
                "{\"key\":\"place.payment\",\"en\":\"Place Order Payment\",\"ru\":\"Оформить оплату заказа\",\"kk\":\"Тапсырыс үшін төлем жасаңыз\"}\n" +
                "{\"key\":\"cancel.order\",\"en\":\"Cancel Order\",\"ru\":\"Отменить заказ\",\"kk\":\"Тапсырыстан бас тарту\"}\n" +
                "{\"key\":\"ship\",\"en\":\"Ship\",\"ru\":\"Отправить\",\"kk\":\"Жіберу\"}\n" +
                "{\"key\":\"close.order\",\"en\":\"Close Order\",\"ru\":\"Закрыть заказ\",\"kk\":\"Тапсырысты жабу\"}\n" +
                "{\"key\":\"change.price\",\"en\":\"Change Price\",\"ru\":\"Изменить цену\",\"kk\":\"Бағаны өзгерту\"}\n" +
                "{\"key\":\"complete.end\",\"en\":\"Aftersale Completion Ends Order\",\"ru\":\"Завершение послепродажного обслуживания завершает заказ\",\"kk\":\"Сатудан кейінгі қызметті аяқтау тапсырысты аяқтайды\"}\n" +
                "{\"key\":\"multi.payment\",\"en\":\"Multi-Stage Payment\",\"ru\":\"Многоступенчатая оплата\",\"kk\":\"Көп сатылы төлем\"}\n" +
                "{\"key\":\"multi.seller\",\"en\":\"Multi-Stage Seller Processing\",\"ru\":\"Многоступенчатая обработка продавцом\",\"kk\":\"Сатушының көп сатылы өңдеуі\"}\n" +
                "{\"key\":\"multi.user\",\"en\":\"Multi-Stage User Confirmation\",\"ru\":\"Многоступенчатое подтверждение пользователем\",\"kk\":\"Пайдаланушының көп сатылы растауы\"}\n" +
                "{\"key\":\"cannot.empty\",\"en\":\"Cannot be Empty\",\"ru\":\"Не может быть пустым\",\"kk\":\"Бос бола алмайды\"}\n" +
                "{\"key\":\"product\",\"en\":\"Product\",\"ru\":\"Продукт\",\"kk\":\"Өнім\"}\n" +
                "{\"key\":\"order.qty.empty\",\"en\":\"Product Order Quantity Cannot be Empty\",\"ru\":\"Количество заказа продукта не может быть пустым\",\"kk\":\"Өнімге тапсырыс беру саны бос бола алмайды\"}\n" +
                "{\"key\":\"order.qty.less\",\"en\":\"Product Order Quantity Cannot be Less Than\",\"ru\":\"Количество заказа продукта не может быть меньше\",\"kk\":\"Өнімге тапсырыс беру саны аз бола алмайды\"}\n" +
                "{\"key\":\"agent.price.less\",\"en\":\"Agent Order Price Cannot be Less Than\",\"ru\":\"Цена заказа агента не может быть меньше\",\"kk\":\"Агент тапсырысының бағасы кем бола алмайды\"}\n" +
                "{\"key\":\"member\",\"en\":\"Member\",\"ru\":\"Участник\",\"kk\":\"Қатысушы\"}\n" +
                "{\"key\":\"order.channel.empty\",\"en\":\"Order Channel Cannot be Empty\",\"ru\":\"Канал заказа не может быть пустым\",\"kk\":\"Тапсырыс арнасы бос бола алмайды\"}\n" +
                "{\"key\":\"product.list.empty\",\"en\":\"Product List Cannot be Empty\",\"ru\":\"Список продуктов не может быть пустым\",\"kk\":\"Өнімдер тізімі бос бола алмайды\"}\n" +
                "{\"key\":\"stage.num.empty\",\"en\":\"Stage Number Cannot be Empty\",\"ru\":\"Номер стадии не может быть пустым\",\"kk\":\"Кезең нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"pay.channel.empty\",\"en\":\"Payment Channel Cannot be Empty\",\"ru\":\"Канал оплаты не может быть пустым\",\"kk\":\"Төлем арнасы бос бола алмайды\"}\n" +
                "{\"key\":\"order.confirm.empty\",\"en\":\"Order Confirmation Information Cannot be Empty\",\"ru\":\"Информация о подтверждении заказа не может быть пустой\",\"kk\":\"Тапсырысты растау туралы ақпарат бос бола алмайды\"}\n" +
                "{\"key\":\"cannot.be\",\"en\":\"Cannot be\",\"ru\":\"Не может быть\",\"kk\":\"Бола алмайды\"}\n" +
                "{\"key\":\"buyer\",\"en\":\"Buyer\",\"ru\":\"Покупатель\",\"kk\":\"Сатып алушы\"}\n" +
                "{\"key\":\"main.order.empty\",\"en\":\"Main Order Number Cannot be Empty\",\"ru\":\"Номер основного заказа не может быть пустым\",\"kk\":\"Негізгі тапсырыс нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"place.channel.empty\",\"en\":\"Order Placement Channel Cannot be Empty\",\"ru\":\"Канал размещения заказа не может быть пустым\",\"kk\":\"Тапсырыс беру арнасы бос бола алмайды\"}\n" +
                "{\"key\":\"pay.num.empty\",\"en\":\"Payment Transaction Number Cannot be Empty\",\"ru\":\"Номер транзакции оплаты не может быть пустым\",\"kk\":\"Төлем транзакциясының нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"logistics.num.empty\",\"en\":\"Logistics Order Number Cannot be Empty\",\"ru\":\"Номер заказа логистики не может быть пустым\",\"kk\":\"Логистикалық тапсырыс нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"customer.id.empty\",\"en\":\"Customer ID Cannot be Empty\",\"ru\":\"Идентификатор клиента не может быть пустым\",\"kk\":\"Клиент идентификаторы бос бола алмайды\"}\n" +
                "{\"key\":\"aftersales.channel.empty\",\"en\":\"Aftersales Application Channel Cannot be Empty\",\"ru\":\"Канал подачи заявки на послепродажное обслуживание не может быть пустым\",\"kk\":\"Сатудан кейінгі қызмет көрсету арнасы бос бола алмайды\"}\n" +
                "{\"key\":\"main.order.num.empty\",\"en\":\"Main Order Number Cannot be Empty\",\"ru\":\"Номер основного заказа не может быть пустым\",\"kk\":\"Негізгі тапсырыс нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"aftersales.main.order\",\"en\":\"Aftersales Main Order\",\"ru\":\"Основной заказ послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі негізгі тапсырыс\"}\n" +
                "{\"key\":\"operate.customer\",\"en\":\"Operate Customer\",\"ru\":\"Операция с клиентом\",\"kk\":\"Клиентпен операция\"}\n" +
                "{\"key\":\"operate.seller\",\"en\":\"Operate Seller\",\"ru\":\"Операция с продавцом\",\"kk\":\"Сатушымен операция\"}\n" +
                "{\"key\":\"cannot.all.empty\",\"en\":\"Cannot be All Empty\",\"ru\":\"Не может быть все пустым\",\"kk\":\"Бәрі бос бола алмайды\"}\n" +
                "{\"key\":\"amount.not.less\",\"en\":\"Amount Cannot be Less Than\",\"ru\":\"Сумма не может быть меньше\",\"kk\":\"Сома аз бола алмайды\"}\n" +
                "{\"key\":\"aftersale.type.empty\",\"en\":\"Aftersale Service Type Cannot be Empty\",\"ru\":\"Тип послепродажного обслуживания не может быть пустым\",\"kk\":\"Сатудан кейінгі қызмет көрсету түрі бос бола алмайды\"}\n" +
                "{\"key\":\"sub.order.empty\",\"en\":\"Sub Order Information Cannot be Empty\",\"ru\":\"Информация о подзаказе не может быть пустой\",\"kk\":\"Қосалқы тапсырыс туралы ақпарат бос бола алмайды\"}\n" +
                "{\"key\":\"refund.total.less\",\"en\":\"Refund Total Amount Cannot be Less Than\",\"ru\":\"Общая сумма возврата не может быть меньше\",\"kk\":\"Қайтарудың жалпы сомасы аз бола алмайды\"}\n" +
                "{\"key\":\"cents\",\"en\":\"Cents\",\"ru\":\"Центы\",\"kk\":\"Центтер\"}\n" +
                "{\"key\":\"refund.reason\",\"en\":\"Refund Reason\",\"ru\":\"Причина возврата\",\"kk\":\"Қайтару себебі\"}\n" +
                "{\"key\":\"goods.received.empty\",\"en\":\"Goods Receipt Status Cannot be Empty\",\"ru\":\"Статус получения товара не может быть пустым\",\"kk\":\"Тауарды алу мәртебесі бос бола алмайды\"}\n" +
                "{\"key\":\"login.member\",\"en\":\"Login Member\",\"ru\":\"Войти как участник\",\"kk\":\"Мүше ретінде кіру\"}\n" +
                "{\"key\":\"add.cart.empty\",\"en\":\"Added Cart Quantity Cannot be Empty\",\"ru\":\"Количество добавленных товаров в корзину не может быть пустым\",\"kk\":\"Себетке қосылған заттардың саны бос бола алмайды\"}\n" +
                "{\"key\":\"logistics.type.empty\",\"en\":\"Logistics Company Type Cannot be Empty\",\"ru\":\"Тип компании по логистике не может быть пустым\",\"kk\":\"Логистикалық компанияның түрі бос бола алмайды\"}\n" +
                "{\"key\":\"invalid.param\",\"en\":\"Invalid Parameter\",\"ru\":\"Неверный параметр\",\"kk\":\"Қате параметр\"}\n" +
                "{\"key\":\"stock.insufficient\",\"en\":\"Insufficient Stock\",\"ru\":\"Недостаток на складе\",\"kk\":\"Қоймадағы кемшілік\"}\n" +
                "{\"key\":\"points.insufficient\",\"en\":\"Insufficient Points\",\"ru\":\"Недостаточно баллов\",\"kk\":\"Ұпайлар жеткіліксіз\"}\n" +
                "{\"key\":\"product.not.exist\",\"en\":\"Product Does Not Exist\",\"ru\":\"Продукт не существует\",\"kk\":\"Өнім жоқ\"}\n" +
                "{\"key\":\"user.not.exist\",\"en\":\"User Does Not Exist\",\"ru\":\"Пользователь не существует\",\"kk\":\"Пайдаланушы жоқ\"}\n" +
                "{\"key\":\"discount.not.exist\",\"en\":\"Selected Discount Does Not Exist\",\"ru\":\"Выбранная скидка не существует\",\"kk\":\"Таңдалған жеңілдік жоқ\"}\n" +
                "{\"key\":\"address.not.exist\",\"en\":\"Shipping Address Does Not Exist or is Invalid\",\"ru\":\"Адрес доставки не существует или является недействительным\",\"kk\":\"Жеткізу мекенжайы жоқ немесе жарамсыз\"}\n" +
                "{\"key\":\"price.mismatch\",\"en\":\"Price Calculation Mismatch\",\"ru\":\"Несоответствие расчета цены\",\"kk\":\"Бағаны есептеудің сәйкессіздігі\"}\n" +
                "{\"key\":\"order.status.invalid\",\"en\":\"Order Status Invalid\",\"ru\":\"Неверный статус заказа\",\"kk\":\"Тапсырыс мәртебесі қате\"}\n" +
                "{\"key\":\"stock.deduct.failed\",\"en\":\"Order Stock Deduction Failed\",\"ru\":\"Ошибка списания товара из заказа\",\"kk\":\"Тапсырыстан тауарды есептен шығару қатесі\"}\n" +
                "{\"key\":\"user.not.match\",\"en\":\"Order User Does Not Match\",\"ru\":\"Пользователь заказа не совпадает\",\"kk\":\"Тапсырыс пайдаланушысы сәйкес келмейді\"}\n" +
                "{\"key\":\"no.permission\",\"en\":\"No Permission\",\"ru\":\"Нет прав доступа\",\"kk\":\"Кіру құқығы жоқ\"}\n" +
                "{\"key\":\"product.not.listed\",\"en\":\"Product Not Listed\",\"ru\":\"Продукт не размещен\",\"kk\":\"Өнім орналастырылмаған\"}\n" +
                "{\"key\":\"order.amount\",\"en\":\"Order Amount\",\"ru\":\"Сумма заказа\",\"kk\":\"Тапсырыс сомасы\"}\n" +
                "{\"key\":\"pay.channel.invalid\",\"en\":\"Payment Channel Invalid\",\"ru\":\"Неверный канал оплаты\",\"kk\":\"Төлем арнасы қате\"}\n" +
                "{\"key\":\"address.unsupported\",\"en\":\"Product Does Not Support User's Shipping Address\",\"ru\":\"Продукт не поддерживает адрес доставки пользователя\",\"kk\":\"Өнім пайдаланушының жеткізу мекенжайын қолдамайды\"}\n" +
                "{\"key\":\"not.sellable\",\"en\":\"Not Sellable\",\"ru\":\"Не продается\",\"kk\":\"Сатылмайды\"}\n" +
                "{\"key\":\"concurrent.order\",\"en\":\"Concurrent Order Submission\",\"ru\":\"Параллельное размещение заказов\",\"kk\":\"Тапсырыстарды қатар орналастыру\"}\n" +
                "{\"key\":\"order.created\",\"en\":\"Order Created\",\"ru\":\"Заказ создан\",\"kk\":\"Тапсырыс жасалды\"}\n" +
                "{\"key\":\"no.duplicate.submit\",\"en\":\"Do Not Submit Duplicate Orders\",\"ru\":\"Не отправлять дублирующиеся заказы\",\"kk\":\"Қайталанатын тапсырыстарды жібермеңіз\"}\n" +
                "{\"key\":\"order.amount.abnormal\",\"en\":\"Abnormal Order Amount\",\"ru\":\"Аномальная сумма заказа\",\"kk\":\"Тапсырыстың аномалдық сомасы\"}\n" +
                "{\"key\":\"no.merge.orders\",\"en\":\"Product Does Not Allow Order Merging\",\"ru\":\"Продукт не позволяет объединять заказы\",\"kk\":\"Өнім тапсырыстарды біріктіруге мүмкіндік бермейді\"}\n" +
                "{\"key\":\"user.status.invalid\",\"en\":\"User Status Cannot Proceed with Transaction\",\"ru\":\"Статус пользователя не позволяет продолжить сделку\",\"kk\":\"Пайдаланушы мәртебесі мәмілені жалғастыруға мүмкіндік бермейді\"}\n" +
                "{\"key\":\"promotion.changed\",\"en\":\"Promotion Changed\",\"ru\":\"Скидка изменена\",\"kk\":\"Жеңілдік өзгертілді\"}\n" +
                "{\"key\":\"order.not.exist\",\"en\":\"Order Does Not Exist\",\"ru\":\"Заказ не существует\",\"kk\":\"Тапсырыс жоқ\"}\n" +
                "{\"key\":\"order.info.incomplete\",\"en\":\"Order Information Incomplete\",\"ru\":\"Информация о заказе неполная\",\"kk\":\"Тапсырыс туралы ақпарат толық емес\"}\n" +
                "{\"key\":\"points.deduct.failed\",\"en\":\"Points Deduction Failed\",\"ru\":\"Ошибка списания баллов\",\"kk\":\"Ұпайларды есептен шығару қатесі\"}\n" +
                "{\"key\":\"query.param.invalid\",\"en\":\"Order Query Parameter Invalid\",\"ru\":\"Неверный параметр запроса заказа\",\"kk\":\"Тапсырыс сұрау параметрінің қатесі\"}\n" +
                "{\"key\":\"query.exec.error\",\"en\":\"Order Query Execution Error\",\"ru\":\"Ошибка выполнения запроса заказа\",\"kk\":\"Тапсырыс сұрауын орындау қатесі\"}\n" +
                "{\"key\":\"order.modify.error\",\"en\":\"Order Modification Status Error\",\"ru\":\"Ошибка состояния изменения заказа\",\"kk\":\"Тапсырысты өзгерту күйінің қатесі\"}\n" +
                "{\"key\":\"order.amount.incorrect\",\"en\":\"Order Amount Incorrect\",\"ru\":\"Неправильная сумма заказа\",\"kk\":\"Тапсырыс сомасы қате\"}\n" +
                "{\"key\":\"order.changed\",\"en\":\"Order Has Changed\",\"ru\":\"Заказ изменен\",\"kk\":\"Тапсырыс өзгертілді\"}\n" +
                "{\"key\":\"confirm.again\",\"en\":\"Please Confirm Again\",\"ru\":\"Пожалуйста, подтвердите снова\",\"kk\":\"Қайта растауыңызды өтінеміз\"}\n" +
                "{\"key\":\"logistics.creation.failed\",\"en\":\"Logistics Information Creation Failed\",\"ru\":\"Ошибка создания логистической информации\",\"kk\":\"Логистикалық ақпаратты құру қатесі\"}\n" +
                "{\"key\":\"logistics.user.not.match\",\"en\":\"Logistics User Does Not Match\",\"ru\":\"Пользователь логистики не совпадает\",\"kk\":\"Логистика пайдаланушысы сәйкес келмейді\"}\n" +
                "{\"key\":\"multi.stage.mismatch\",\"en\":\"Multi-Stage Calculation Mismatch\",\"ru\":\"Несоответствие многослойного расчета\",\"kk\":\"Көп қабатты есептеудің сәйкес келмеуі\"}\n" +
                "{\"key\":\"stage.num.incorrect\",\"en\":\"Stage Number Incorrect\",\"ru\":\"Неверный номер стадии\",\"kk\":\"Кезең нөмірі қате\"}\n" +
                "{\"key\":\"not.multi.stage\",\"en\":\"Not a Multi-Stage Order\",\"ru\":\"Не многослойный заказ\",\"kk\":\"Көп қабатты тапсырыс емес\"}\n" +
                "{\"key\":\"stage.status.incorrect\",\"en\":\"Stage Status Incorrect\",\"ru\":\"Неверный статус стадии\",\"kk\":\"Сатының мәртебесі қате\"}\n" +
                "{\"key\":\"order.info.changed\",\"en\":\"Order Information Has Changed\",\"ru\":\"Информация о заказе изменилась\",\"kk\":\"Тапсырыс туралы ақпарат өзгерді\"}\n" +
                "{\"key\":\"multi.stage.config.error\",\"en\":\"Multi-Stage Template Configuration Incorrect\",\"ru\":\"Неверная конфигурация шаблона многослойного заказа\",\"kk\":\"Көп қабатты тапсырыс үлгісінің конфигурациясы дұрыс емес\"}\n" +
                "{\"key\":\"multi.stage.data.error\",\"en\":\"Multi-Stage Submitted Data Incorrect\",\"ru\":\"Неверные данные, отправленные в многослойный заказ\",\"kk\":\"Көп қабатты тапсырысқа жіберілген қате деректер\"}\n" +
                "{\"key\":\"review.main.order\",\"en\":\"Review Main Order\",\"ru\":\"Основной заказ на проверку\",\"kk\":\"Тексеруге арналған негізгі тапсырыс\"}\n" +
                "{\"key\":\"not.match\",\"en\":\"Not a Match\",\"ru\":\"Не совпадает\",\"kk\":\"Сәйкес келмейді\"}\n" +
                "{\"key\":\"cannot.review\",\"en\":\"Current Order Cannot be Reviewed\",\"ru\":\"Этот заказ нельзя проверить\",\"kk\":\"Бұл тапсырысты тексеру мүмкін емес\"}\n" +
                "{\"key\":\"review.not.exist\",\"en\":\"Review Record Does Not Exist\",\"ru\":\"Запись проверки не существует\",\"kk\":\"Тексеру жазбасы жоқ\"}\n" +
                "{\"key\":\"trade.system.error\",\"en\":\"Trade System Error\",\"ru\":\"Ошибка торговой системы\",\"kk\":\"Сауда жүйесінің қателігі\"}\n" +
                "{\"key\":\"server.error\",\"en\":\"Server Error\",\"ru\":\"Ошибка сервера\",\"kk\":\"Сервер қатесі\"}\n" +
                "{\"key\":\"no.other.data\",\"en\":\"Cannot Operate on Other's Data\",\"ru\":\"Невозможно управлять чужими данными\",\"kk\":\"Басқа адамдардың деректерін басқару мүмкін емес\"}\n" +
                "{\"key\":\"concurrent.modify.error\",\"en\":\"Concurrent Modification Error\",\"ru\":\"Ошибка параллельного изменения\",\"kk\":\"Параллельді өзгерту қатесі\"}\n" +
                "{\"key\":\"try.later\",\"en\":\"Try Again Later\",\"ru\":\"Повторите попытку позже\",\"kk\":\"Кейінірек қайталап көріңіз\"}\n" +
                "{\"key\":\"pay.order.not.exist\",\"en\":\"Payment Order Does Not Exist\",\"ru\":\"Платежный заказ не существует\",\"kk\":\"Төлем тапсырысы жоқ\"}\n" +
                "{\"key\":\"pay.order.not.found\",\"en\":\"Payment Order Corresponding to Order Not Found\",\"ru\":\"Платежный заказ, соответствующий заказу, не найден\",\"kk\":\"Тапсырысқа сәйкес келетін төлем тапсырысы табылмады\"}\n" +
                "{\"key\":\"pay.order.status.error\",\"en\":\"Payment Order and Order Must Be in Pending Status\",\"ru\":\"Платежный заказ и заказ должны быть в статусе ожидания\",\"kk\":\"Төлем тапсырысы мен тапсырысы күту күйінде болуы керек\"}\n" +
                "{\"key\":\"pay.channel.unsupported\",\"en\":\"No Supported Payment Channels for This Payment\",\"ru\":\"Для этого платежа нет поддерживаемых каналов оплаты\",\"kk\":\"Бұл төлем үшін қолдау көрсетілетін төлем арналары жоқ\"}\n" +
                "{\"key\":\"duplicate.payment\",\"en\":\"Duplicate Payment\",\"ru\":\"Дублирующий платеж\",\"kk\":\"Қайталанатын төлем\"}\n" +
                "{\"key\":\"pay.amount.incorrect\",\"en\":\"Actual Payment Amount Incorrect\",\"ru\":\"Неверная сумма фактического платежа\",\"kk\":\"Нақты төлемнің сомасы қате\"}\n" +
                "{\"key\":\"db.error\",\"en\":\"Database Error\",\"ru\":\"Ошибка базы данных\",\"kk\":\"Дерекқор қатесі\"}\n" +
                "{\"key\":\"pay.timeout\",\"en\":\"Payment Timeout\",\"ru\":\"Тайм-аут платежа\",\"kk\":\"Төлемнің тайм-ауты\"}\n" +
                "{\"key\":\"pay.status.invalid\",\"en\":\"Payment Order Status Invalid\",\"ru\":\"Неверный статус платежного заказа\",\"kk\":\"Төлем тапсырысының мәртебесі қате\"}\n" +
                "{\"key\":\"pay.callback.failed\",\"en\":\"Payment Callback Failed\",\"ru\":\"Ошибка обратного вызова платежа\",\"kk\":\"Төлемді кері шақыру қатесі\"}\n" +
                "{\"key\":\"pay.link.error\",\"en\":\"Payment Link Construction Error\",\"ru\":\"Ошибка конструирования платежной ссылки\",\"kk\":\"Төлем сілтемесін жобалау қатесі\"}\n" +
                "{\"key\":\"points.deduct.error\",\"en\":\"Points Deduction Error\",\"ru\":\"Ошибка списания баллов\",\"kk\":\"Ұпайларды есептен шығару қатесі\"}\n" +
                "{\"key\":\"pay.tx.not.exist\",\"en\":\"Payment Transaction Does Not Exist\",\"ru\":\"Платежная транзакция не существует\",\"kk\":\"Төлем транзакциясы жоқ\"}\n" +
                "{\"key\":\"refund.detail.not.exist\",\"en\":\"Refund Order Detail Does Not Exist\",\"ru\":\"Детали возврата платежа не существуют\",\"kk\":\"Төлемді қайтару туралы мәліметтер жоқ\"}\n" +
                "{\"key\":\"cannot.initiate.refund\",\"en\":\"Cannot Initiate Refund\",\"ru\":\"Невозможно инициировать возврат\",\"kk\":\"Қайтаруды бастау мүмкін емес\"}\n" +
                "{\"key\":\"refund.completed\",\"en\":\"Refund Completed\",\"ru\":\"Возврат завершен\",\"kk\":\"Қайтару аяқталды\"}\n" +
                "{\"key\":\"duplicate.operation\",\"en\":\"Duplicate Operation\",\"ru\":\"Дублирующая операция\",\"kk\":\"Қайталанатын операция\"}\n" +
                "{\"key\":\"pay.gateway.error\",\"en\":\"Payment Gateway Request Error\",\"ru\":\"Ошибка запроса к платежному шлюзу\",\"kk\":\"Төлем шлюзіне сұрау салу қатесі\"}\n" +
                "{\"key\":\"update.refund.failed\",\"en\":\"Update Refund Order Detail Failed\",\"ru\":\"Ошибка обновления деталях возврата\",\"kk\":\"Қайтару мәліметтерін жаңарту қатесі\"}\n" +
                "{\"key\":\"pay.callback.verif.failed\",\"en\":\"Payment Callback Verification Failed\",\"ru\":\"Ошибка проверки обратного вызова платежа\",\"kk\":\"Төлемді кері шақыруды тексеру қатесі\"}\n" +
                "{\"key\":\"refund.pay.callback.failed\",\"en\":\"Refund Payment Callback Failed\",\"ru\":\"Ошибка обратного вызова возврата средств\",\"kk\":\"Қаражатты қайтару қатесі\"}\n" +
                "{\"key\":\"pay.failed\",\"en\":\"Payment Failed\",\"ru\":\"Платеж не прошел\",\"kk\":\"Төлем өтпеді\"}\n" +
                "{\"key\":\"send.refund.success.msg.failed\",\"en\":\"Send Refund Success Message Failed\",\"ru\":\"Не удалось отправить сообщение об успешном возврате\",\"kk\":\"Сәтті қайтару туралы хабарламаны жіберу мүмкін болмады\"}\n" +
                "{\"key\":\"stock.lock.failed\",\"en\":\"Stock Lock Failed\",\"ru\":\"Ошибка блокировки товара\",\"kk\":\"Тауарды бұғаттау қатесі\"}\n" +
                "{\"key\":\"pay.amount.check.error\",\"en\":\"Payment Amount Check Error\",\"ru\":\"Ошибка проверки суммы платежа\",\"kk\":\"Төлем сомасын тексеру қатесі\"}\n" +
                "{\"key\":\"full.points.deduct\",\"en\":\"Full Points Deduction Selected\",\"ru\":\"Выбрано полное списание баллов\",\"kk\":\"Ұпайларды толық есептен шығару таңдалды\"}\n" +
                "{\"key\":\"actual.pay.incorrect\",\"en\":\"Actual Payment Amount Incorrect\",\"ru\":\"Неверная фактическая сумма платежа\",\"kk\":\"Төлемнің нақты сомасы қате\"}\n" +
                "{\"key\":\"refund.points.not.exist\",\"en\":\"Points Deduction Refund Detail Does Not Exist\",\"ru\":\"Детали возврата баллов не существуют\",\"kk\":\"Ұпайларды қайтару туралы мәліметтер жоқ\"}\n" +
                "{\"key\":\"points.refund.error\",\"en\":\"Points Refund Error\",\"ru\":\"Ошибка возврата баллов\",\"kk\":\"Ұпайларды қайтару қатесі\"}\n" +
                "{\"key\":\"update.refund.detail.failed\",\"en\":\"Update Refund Detail Failed\",\"ru\":\"Ошибка обновления деталей возврата\",\"kk\":\"Қайтару мәліметтерін жаңарту қатесі\"}\n" +
                "{\"key\":\"refund.update.status.failed\",\"en\":\"Refund Update Order or Aftersale Status Failed\",\"ru\":\"Ошибка обновления статуса заказа или послепродажного обслуживания\",\"kk\":\"Тапсырыс күйін жаңарту немесе сатудан кейінгі қызмет көрсету қатесі\"}\n" +
                "{\"key\":\"refund.update.stock.failed\",\"en\":\"Refund Update Stock Failed\",\"ru\":\"Ошибка обновления склада при возврате\",\"kk\":\"Қайтару кезінде қойманы жаңарту қатесі\"}\n" +
                "{\"key\":\"merge.pay.failed\",\"en\":\"Merge Payment Failed\",\"ru\":\"Ошибка слияния платежей\",\"kk\":\"Төлемдерді біріктіру қатесі\"}\n" +
                "{\"key\":\"pay.query.param.error\",\"en\":\"Payment Query Parameter Error\",\"ru\":\"Ошибка параметров запроса платежа\",\"kk\":\"Төлем сұрауы параметрлерінің қатесі\"}\n" +
                "{\"key\":\"pay.query.exec.error\",\"en\":\"Payment Query Execution Error\",\"ru\":\"Ошибка выполнения запроса платежа\",\"kk\":\"Төлем сұрауын орындау қатесі\"}\n" +
                "{\"key\":\"no.change.pay.method\",\"en\":\"Cannot Change Payment Method\",\"ru\":\"Невозможно изменить метод оплаты\",\"kk\":\"Төлем әдісін өзгерту мүмкін емес\"}\n" +
                "{\"key\":\"cart.stock.insufficient\",\"en\":\"Insufficient Cart Stock\",\"ru\":\"Недостаточно товаров в корзине\",\"kk\":\"Себеттегі тауарлар жеткіліксіз\"}\n" +
                "{\"key\":\"cart.full\",\"en\":\"Cart is Full\",\"ru\":\"Корзина заполнена\",\"kk\":\"Себет толы\"}\n" +
                "{\"key\":\"qty.limit.exceed\",\"en\":\"Quantity Limit Exceeded\",\"ru\":\"Превышен лимит по количеству\",\"kk\":\"Саны бойынша лимиттен асып кетті\"}\n" +
                "{\"key\":\"apply.aftersale.forbid\",\"en\":\"Cannot Apply for Aftersale\",\"ru\":\"Невозможно подать заявку на послепродажное обслуживание\",\"kk\":\"Сатудан кейінгі қызмет көрсетуге өтініш беру мүмкін емес\"}\n" +
                "{\"key\":\"aftersale.time.exceed\",\"en\":\"Aftersale Application Time Exceeded\",\"ru\":\"Время подачи заявки на послепродажное обслуживание истекло\",\"kk\":\"Сатудан кейінгі қызмет көрсетуге өтініш беру уақыты аяқталды\"}\n" +
                "{\"key\":\"aftersale.qty.exceed\",\"en\":\"Aftersale Product Quantity Exceeded\",\"ru\":\"Превышено количество товара для послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі қызмет көрсету үшін тауарлар саны асып кетті\"}\n" +
                "{\"key\":\"refund.amount.exceed\",\"en\":\"Refund Amount Exceeded\",\"ru\":\"Превышена сумма возврата\",\"kk\":\"Қайтару сомасы асып кетті\"}\n" +
                "{\"key\":\"refund.amount.allocate.fail\",\"en\":\"Cannot Allocate Refund Amount\",\"ru\":\"Невозможно распределить сумму возврата\",\"kk\":\"Қайтару сомасын бөлу мүмкін емес\"}\n" +
                "{\"key\":\"modify.amount\",\"en\":\"Please Modify Amount\",\"ru\":\"Пожалуйста, измените сумму\",\"kk\":\"Соманы өзгертіңіз\"}\n" +
                "{\"key\":\"invalid.refund.reason\",\"en\":\"Invalid Refund Reason\",\"ru\":\"Неверная причина возврата\",\"kk\":\"Қайтарудың себебі қате\"}\n" +
                "{\"key\":\"only.full.refund\",\"en\":\"Only Full Refund Allowed\",\"ru\":\"Разрешен только полный возврат\",\"kk\":\"Тек толық қайтаруға рұқсат етіледі\"}\n" +
                "{\"key\":\"aftersale.not.exist\",\"en\":\"Aftersale Order Does Not Exist\",\"ru\":\"Заказ послепродажного обслуживания не существует\",\"kk\":\"Сатудан кейінгі қызмет көрсету тапсырыс жоқ\"}\n" +
                "{\"key\":\"aftersale.status.incorrect\",\"en\":\"Aftersale Order Status Incorrect\",\"ru\":\"Неверный статус заказа послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі қызмет көрсету тапсырысының мәртебесі қате\"}\n" +
                "{\"key\":\"refund.order.not.exist\",\"en\":\"Refund Order Does Not Exist\",\"ru\":\"Заказ возврата не существует\",\"kk\":\"Қайтару тапсырысы жоқ\"}\n" +
                "{\"key\":\"refund.points.failed\",\"en\":\"Aftersale Points Refund Failed\",\"ru\":\"Ошибка возврата баллов послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі ұпайларды қайтару қатесі\"}\n" +
                "{\"key\":\"query.aftersale.info.failed\",\"en\":\"Query Aftersale Order Information Failed\",\"ru\":\"Ошибка запроса информации о заказе послепродажного обслуживания\",\"kk\":\"Сатудан кейінгі тапсырыс туралы ақпаратты сұрау қатесі\"}\n" +
                "{\"key\":\"reserved.stock\",\"en\":\"Reserved Stock\",\"ru\":\"Зарезервированный товар\",\"kk\":\"Резервтелген тауар\"}\n" +
                "{\"key\":\"current.status\",\"en\":\"Current Status\",\"ru\":\"Текущий статус\",\"kk\":\"Ағымдағы мәртебе\"}\n" +
                "{\"key\":\"deduct.stock\",\"en\":\"Deduct Stock\",\"ru\":\"Списание товара со склада\",\"kk\":\"Тауарды қоймадан есептен шығару\"}\n" +
                "{\"key\":\"cannot.change.status\",\"en\":\"Cannot Change to Target Status\",\"ru\":\"Невозможно изменить на целевой статус\",\"kk\":\"Мақсатты мәртебеге өзгерту мүмкін емес\"}\n" +
                "{\"key\":\"lock\",\"en\":\"Lock\",\"ru\":\"Блокировка\",\"kk\":\"Бұғаттау\"}\n" +
                "{\"key\":\"release.stock\",\"en\":\"Release Reserved Stock\",\"ru\":\"Освобождение зарезервированного товара\",\"kk\":\"Резервтелген тауарды босату\"}\n" +
                "{\"key\":\"replenish.stock\",\"en\":\"Replenish Stock\",\"ru\":\"Пополнение товара\",\"kk\":\"Тауарды толықтыру\"}\n" +
                "{\"key\":\"missing\",\"en\":\"Missing\",\"ru\":\"Товар отсутствует\",\"kk\":\"Тауар жоқ\"}\n" +
                "{\"key\":\"query.sku\",\"en\":\"Query SKU\",\"ru\":\"Ошибка запроса SKU\",\"kk\":\"SKU сұрау қатесі\"}\n" +
                "{\"key\":\"fixed.price\",\"en\":\"Fixed Price\",\"ru\":\"Фиксированная цена\",\"kk\":\"Белгіленген баға\"}\n" +
                "{\"key\":\"discount\",\"en\":\"Discount\",\"ru\":\"Скидка\",\"kk\":\"Жеңілдік\"}\n" +
                "{\"key\":\"cart.discount.query\",\"en\":\"Cart Discount Query\",\"ru\":\"Запрос на скидку корзины\",\"kk\":\"Себетке жеңілдік сұрау\"}\n" +
                "{\"key\":\"confirm.discount.query\",\"en\":\"Confirm Order Discount Query\",\"ru\":\"Запрос на скидку при подтверждении заказа\",\"kk\":\"Тапсырысты растау кезінде жеңілдік сұрау\"}\n" +
                "{\"key\":\"create.discount.query\",\"en\":\"Create Order Discount Query\",\"ru\":\"Запрос на скидку при создании заказа\",\"kk\":\"Тапсырыс жасау кезінде жеңілдік сұрау\"}\n" +
                "{\"key\":\"deduct.promo.asset\",\"en\":\"Deduct Promotional Asset\",\"ru\":\"Списание рекламных активов\",\"kk\":\"Жарнамалық активтерді есептен шығару\"}\n" +
                "{\"key\":\"rollback.promo.asset\",\"en\":\"Rollback Promotional Asset\",\"ru\":\"Откат рекламных активов\",\"kk\":\"Жарнамалық активтерді қайтару\"}\n" +
                "{\"key\":\"no.query.param\",\"en\":\"No Query Parameter\",\"ru\":\"Нет параметра запроса\",\"kk\":\"Сұрау параметрі жоқ\"}\n" +
                "{\"key\":\"initiate.payment\",\"en\":\"Initiate Payment\",\"ru\":\"Инициация платежа\",\"kk\":\"Төлемді бастау\"}\n" +
                "{\"key\":\"is.empty\",\"en\":\"Is Empty\",\"ru\":\"Пусто\",\"kk\":\"Бос\"}\n" +
                "{\"key\":\"initiate.merge.payment\",\"en\":\"Initiate Merge Payment\",\"ru\":\"Инициация слияния платежей\",\"kk\":\"Төлемдерді біріктіруді бастау\"}\n" +
                "{\"key\":\"query.pay.tx\",\"en\":\"Query Payment Transaction\",\"ru\":\"Запрос на платежную транзакцию\",\"kk\":\"Төлем транзакциясын сұрау\"}\n" +
                "{\"key\":\"confirm.pay\",\"en\":\"Confirm Payment\",\"ru\":\"Подтверждение платежа\",\"kk\":\"Төлемді растау\"}\n" +
                "{\"key\":\"cancel.pay\",\"en\":\"Cancel Payment\",\"ru\":\"Отмена платежа\",\"kk\":\"Төлемді жою\"}\n" +
                "{\"key\":\"settlement\",\"en\":\"Settlement\",\"ru\":\"Расчет\",\"kk\":\"Есептеу\"}\n" +
                "{\"key\":\"initiate.refund\",\"en\":\"Initiate Refund\",\"ru\":\"Инициация возврата\",\"kk\":\"Қайтару бастамасы\"}\n" +
                "{\"key\":\"query.pay.order\",\"en\":\"Query Payment Order\",\"ru\":\"Запрос на платежный заказ\",\"kk\":\"Төлем тапсырысына сұрау салу\"}\n" +
                "{\"key\":\"points.query\",\"en\":\"Points Query\",\"ru\":\"Запрос на баллы\",\"kk\":\"Ұпай сұрау\"}\n" +
                "{\"key\":\"points.freeze\",\"en\":\"Points Freeze\",\"ru\":\"Заморозка баллов\",\"kk\":\"Ұпайларды мұздату\"}\n" +
                "{\"key\":\"points.unfreeze\",\"en\":\"Points Unfreeze\",\"ru\":\"Размораживание баллов\",\"kk\":\"Ұпайларды жібіту\"}\n" +
                "{\"key\":\"query.points.rate\",\"en\":\"Query Points Rate\",\"ru\":\"Запрос количества баллов\",\"kk\":\"Ұпай санын сұрау\"}\n" +
                "{\"key\":\"product.cache.query\",\"en\":\"Product Cache Query\",\"ru\":\"Запрос кэша товаров\",\"kk\":\"Тауар кэшін сұрау\"}\n" +
                "{\"key\":\"shipping.fee.query\",\"en\":\"Shipping Fee Query\",\"ru\":\"Запрос на стоимость доставки\",\"kk\":\"Жеткізу құнын сұрау\"}\n" +
                "{\"key\":\"query\",\"en\":\"Query\",\"ru\":\"Запрос\",\"kk\":\"Сұрау\"}\n" +
                "{\"key\":\"product.query\",\"en\":\"Product Query\",\"ru\":\"Запрос на продукт\",\"kk\":\"Өнімге сұраныс\"}\n" +
                "{\"key\":\"category.query\",\"en\":\"Category Query\",\"ru\":\"Запрос на категорию\",\"kk\":\"Санатқа сұрау салу\"}\n" +
                "{\"key\":\"user.query\",\"en\":\"User Query\",\"ru\":\"Запрос на пользователя\",\"kk\":\"Пайдаланушыға сұрау салу\"}\n" +
                "{\"key\":\"seller.query\",\"en\":\"Seller Query\",\"ru\":\"Запрос на продавца\",\"kk\":\"Сатушыға сұрау салу\"}\n" +
                "{\"key\":\"address.query\",\"en\":\"Address Query\",\"ru\":\"Запрос на адрес\",\"kk\":\"Мекенжайға сұрау салу\"}\n" +
                "{\"key\":\"default.address.query\",\"en\":\"Default Address Query\",\"ru\":\"Запрос на основной адрес\",\"kk\":\"Негізгі мекенжайға сұрау салу\"}\n" +
                "{\"key\":\"exception\",\"en\":\"Exception\",\"ru\":\"Исключение\",\"kk\":\"Ерекшелік\"}\n" +
                "{\"key\":\"seller.agree\",\"en\":\"Seller Agreed\",\"ru\":\"Продавец согласен\",\"kk\":\"Сатушы келіседі\"}\n" +
                "{\"key\":\"buyer.ship\",\"en\":\"Buyer Shipped\",\"ru\":\"Покупатель отправил товар\",\"kk\":\"Сатып алушы тауарды жіберді\"}\n" +
                "{\"key\":\"seller.confirm.receipt\",\"en\":\"Seller Confirm Receipt\",\"ru\":\"Продавец подтвердил получение\",\"kk\":\"Сатушы алуды растады\"}\n" +
                "{\"key\":\"seller.reject.receipt\",\"en\":\"Seller Rejected Receipt\",\"ru\":\"Продавец отклонил получение\",\"kk\":\"Сатушы қабылдаудан бас тартты\"}\n" +
                "{\"key\":\"customer\",\"en\":\"Customer\",\"ru\":\"Клиент\",\"kk\":\"Клиент\"}\n" +
                "{\"key\":\"address.empty\",\"en\":\"Address Cannot be Empty\",\"ru\":\"Адрес не может быть пустым\",\"kk\":\"Мекенжай бос бола алмайды\"}\n" +
                "{\"key\":\"phone.empty\",\"en\":\"Phone Number Cannot be Empty\",\"ru\":\"Телефон не может быть пустым\",\"kk\":\"Телефон бос бола алмайды\"}\n" +
                "{\"key\":\"receiver.empty\",\"en\":\"Receiver Cannot be Empty\",\"ru\":\"Получатель не может быть пустым\",\"kk\":\"Алушы бос бола алмайды\"}\n" +
                "{\"key\":\"search.failed\",\"en\":\"Search Failed\",\"ru\":\"Ошибка поиска\",\"kk\":\"Іздеу қатесі\"}\n" +
                "{\"key\":\"pagination.limit.exceed\",\"en\":\"Pagination Parameter Exceeded Limit\",\"ru\":\"Превышен лимит параметров пагинации\",\"kk\":\"Пагинация параметрлерінің лимитінен асып кетті\"}\n" +
                "{\"key\":\"order.exception\",\"en\":\"Order Exception\",\"ru\":\"Исключение заказа\",\"kk\":\"Тапсырысты алып тастау\"}\n" +
                "{\"key\":\"multi.stage.template.not.exist\",\"en\":\"Multi-Stage Template Does Not Exist\",\"ru\":\"Шаблон многослойного заказа не существует\",\"kk\":\"Көп қабатты тапсырыс үлгісі жоқ\"}\n" +
                "{\"key\":\"multi.stage.price.error\",\"en\":\"Multi-Stage Price Parameter Incorrect\",\"ru\":\"Неверный параметр многослойной цены\",\"kk\":\"Көп қабатты баға параметрі қате\"}\n" +
                "{\"key\":\"order.business.identity.exception\",\"en\":\"Order Business Identity Exception\",\"ru\":\"Исключение бизнес-идентификатора заказа\",\"kk\":\"Тапсырыстың бизнес идентификаторын алып тастау\"}\n" +
                "{\"key\":\"business.identity.empty\",\"en\":\"Business Identity Cannot be Empty\",\"ru\":\"Бизнес-идентификатор не может быть пустым\",\"kk\":\"Бизнес-идентификаторы бос бола алмайды\"}\n" +
                "{\"key\":\"seller.no.default.return.address\",\"en\":\"Seller Has No Default Return Address\",\"ru\":\"У продавца нет основного адреса для возврата\",\"kk\":\"Сатушының қайтару үшін негізгі мекенжайы жоқ\"}\n" +
                "{\"key\":\"system.auto.review\",\"en\":\"System Auto Review\",\"ru\":\"Система автоматически проверяет\",\"kk\":\"Жүйе автоматты түрде тексереді\"}\n" +
                "{\"key\":\"request.success\",\"en\":\"Request Successful\",\"ru\":\"Запрос успешен\",\"kk\":\"Сұрау сәтті\"}\n" +
                "{\"key\":\"payment.completed\",\"en\":\"Payment Completed\",\"ru\":\"Платеж завершен\",\"kk\":\"Төлем аяқталды\"}\n" +
                "{\"key\":\"transaction.success\",\"en\":\"Transaction Successful\",\"ru\":\"Сделка успешна\",\"kk\":\"Мәміле сәтті\"}\n" +
                "{\"key\":\"aftersale.closed\",\"en\":\"Aftersale Closed\",\"ru\":\"Послепродажное обслуживание завершено\",\"kk\":\"Сатудан кейінгі қызмет көрсету аяқталды\"}\n" +
                "{\"key\":\"awaiting.seller.confirmation\",\"en\":\"Waiting for Seller Confirmation\",\"ru\":\"Ожидание подтверждения продавца\",\"kk\":\"Сатушының растауын күту\"}\n" +
                "{\"key\":\"awaiting.seller.payment.confirmation\",\"en\":\"Waiting for Seller Payment Confirmation\",\"ru\":\"Ожидание подтверждения платежа продавцом\",\"kk\":\"Сатушының төлемді растауын күту\"}\n" +
                "{\"key\":\"awaiting.buyer.payment.confirmation\",\"en\":\"Waiting for Buyer Payment Confirmation\",\"ru\":\"Ожидание подтверждения платежа покупателем\",\"kk\":\"Сатып алушының төлемді растауын күту\"}\n" +
                "{\"key\":\"cancellation.pending\",\"en\":\"Cancellation Pending\",\"ru\":\"Ожидание отмены\",\"kk\":\"Бас тартуды күту\"}\n" +
                "{\"key\":\"merchant.agreed.cancellation\",\"en\":\"Merchant Agreed Cancellation\",\"ru\":\"Продавец согласен с отменой\",\"kk\":\"Сатушы бас тартуға келіседі\"}\n" +
                "{\"key\":\"cancellation.request.refund\",\"en\":\"Cancellation Request Refund\",\"ru\":\"Запрос на отмену с возвратом\",\"kk\":\"Қайтару туралы сұрау\"}\n" +
                "{\"key\":\"cancellation.request.no.refund\",\"en\":\"Cancellation Request No Refund\",\"ru\":\"Запрос на отмену без возврата\",\"kk\":\"Қайтарусыз бас тарту туралы сұрау\"}\n" +
                "{\"key\":\"merchant.agreed\",\"en\":\"Merchant Agreed\",\"ru\":\"Согласовано с продавцом\",\"kk\":\"Сатушымен келісілген\"}\n" +
                "{\"key\":\"merchant.agreed.no.refund\",\"en\":\"Merchant Agreed No Refund\",\"ru\":\"Отсутствие возможно возврата согласовано с продавцом\",\"kk\":\"Қайтарудың болмауы сатушымен келісілген\"}\n" +
                "{\"key\":\"merchant.agreed.return.info\",\"en\":\"Merchant Agreed Return Information\",\"ru\":\"Информация о возврате согласована с продавцом\",\"kk\":\"Қайтару туралы ақпарат сатушымен келісілген\"}\n" +
                "{\"key\":\"merchant.rejected\",\"en\":\"Merchant Rejected\",\"ru\":\"Отклонено продавцом\",\"kk\":\"Сатушы қабылдамады\"}\n" +
                "{\"key\":\"buyer.cancelled\",\"en\":\"Buyer Cancelled\",\"ru\":\"Отменено покупателем\",\"kk\":\"Сатып алушы жойды\"}\n" +
                "{\"key\":\"buyer.shipped\",\"en\":\"Buyer Shipped\",\"ru\":\"Покупатель отправил товар\",\"kk\":\"Сатып алушы тауарды жіберді\"}\n" +
                "{\"key\":\"merchant.confirmed.receipt\",\"en\":\"Merchant Confirmed Receipt\",\"ru\":\"Продавец подтвердил получение\",\"kk\":\"Сатушы қабылдауды растады\"}\n" +
                "{\"key\":\"merchant.rejected.receipt\",\"en\":\"Merchant Rejected Receipt\",\"ru\":\"Продавец отклонил получение\",\"kk\":\"Сатушы қабылдаудан бас тартты\"}\n" +
                "{\"key\":\"customer.id.required\",\"en\":\"Customer ID Cannot Be Empty\",\"ru\":\"Идентификатор клиента не может быть пустым\",\"kk\":\"Клиент идентификаторы бос бола алмайды\"}\n" +
                "{\"key\":\"merchant.id.required\",\"en\":\"Merchant ID Cannot Be Empty\",\"ru\":\"Идентификатор продавца не может быть пустым\",\"kk\":\"Сатушы идентификаторы бос бола алмайды\"}\n" +
                "{\"key\":\"shipping.address.required\",\"en\":\"Shipping Address Cannot Be Empty\",\"ru\":\"Адрес доставки не может быть пустым\",\"kk\":\"Жеткізу мекенжайы бос бола алмайды\"}\n" +
                "{\"key\":\"contact.phone.required\",\"en\":\"Contact Phone Number Cannot Be Empty\",\"ru\":\"Номер телефона контакта не может быть пустым\",\"kk\":\"Контактінің телефон нөмірі бос бола алмайды\"}\n" +
                "{\"key\":\"receiver.required\",\"en\":\"Receiver Cannot Be Empty\",\"ru\":\"Получатель не может быть пустым\",\"kk\":\"Алушы бос бола алмайды\"}\n" +
                "{\"key\":\"pre.sales\",\"en\":\"Pre-sales\",\"ru\":\"Предпродажа\",\"kk\":\"Алдын ала сату\"}\n" +
                "{\"key\":\"in.sales\",\"en\":\"In-sales\",\"ru\":\"Продажа\",\"kk\":\"Сату\"}\n" +
                "{\"key\":\"after.sales\",\"en\":\"After-sales\",\"ru\":\"Послепродажное обслуживание\",\"kk\":\"Сатудан кейінгі қызмет\"}\n" +
                "{\"key\":\"call.promotion.center.fail\",\"en\":\"Call Promotion Center Failed\",\"ru\":\"Ошибка запроса в центр продвижения\",\"kk\":\"Жарнамалық орталыққа сұрау салу қатесі\"}\n" +
                "{\"key\":\"cart.presale.not.support\",\"en\":\"Unable to Add Presale Product to Cart\",\"ru\":\"Невозможно добавить товар предзаказа в корзину\",\"kk\":\"Себетке алдын ала тапсырыс затын қосу мүмкін емес\"}\n" +
                "{\"key\":\"cart.seckill.not.support\",\"en\":\"Unable to Add Seckill Product to Cart\",\"ru\":\"Невозможно добавить товар распродажи в корзину\",\"kk\":\"Себетке сату өнімін қосу мүмкін емес\"}\n" +
                "{\"key\":\"cart.award.not.support\",\"en\":\"Unable to Add Award Product to Cart\",\"ru\":\"Невозможно добавить товар награды в корзину\",\"kk\":\"Сыйлықты себетке қосу мүмкін емес\"}\n" +
                "{\"key\":\"loan.over.limit\",\"en\":\"Loan Amount Over Limit\",\"ru\":\"Сумма займа превышает лимит\",\"kk\":\"Қарыз сомасы лимиттен асады\"}\n" +
                "{\"key\":\"installment.over.limit\",\"en\":\"Installment Amount Over Limit\",\"ru\":\"Сумма рассрочки превышает лимит\",\"kk\":\"Бөліп төлеу сомасы лимиттен асады\"}\n" +
                "{\"key\":\"cannot.order.same.time\",\"en\":\"Unable to Order Award, Presale, Seckill at the Same Time\",\"ru\":\"Невозможно заказать товар награды, предзаказа или распродажу одновременно\",\"kk\":\"Бір уақытта сыйақы, алдын ала тапсырыс немесе сатылымға тапсырыс беру мүмкін емес\"}\n" +
                "{\"key\":\"pay.model.illegal\",\"en\":\"Payment Model Illegal\",\"ru\":\"Неверная модель оплаты\",\"kk\":\"Төлем моделі қате\"}\n" +
                "{\"key\":\"order.item.null\",\"en\":\"Order Item Can not be Null\",\"ru\":\"Товар заказа не может быть пустым\",\"kk\":\"Тапсырыс заты бос болуы мүмкін емес\"}\n" +
                "{\"key\":\"order.item.illegal\",\"en\":\"Order Item Illegal\",\"ru\":\"Неверный товар заказа\",\"kk\":\"Тапсырыс тауары қате\"}\n" +
                "{\"key\":\"place.orders\",\"en\":\"Place Orders\",\"ru\":\"Размещение заказов\",\"kk\":\"Тапсырыстарды орналастыру\"}\n" +
                "{\"key\":\"expired.or.invalid\",\"en\":\"Expired or Invalid\",\"ru\":\"Истекший или недействительный\",\"kk\":\"Мерзімі өткен немесе жарамсыз\"}]";

        JSONArray jsonObj = JSON.parseArray(jsonArray);
        List<Map<String, String>> langList = new ArrayList<>();
        Map<String, String> en = new HashMap<>();
        en.put("lang", "en");
        en.put("path", "messages_mall_trade_platform_en.properties");
        langList.add(en);

        Map<String, String> kk = new HashMap<>();
        kk.put("lang", "kk");
        kk.put("path", "messages_mall_trade_platform_kk.properties");
        langList.add(kk);

        Map<String, String> ru = new HashMap<>();
        ru.put("lang", "ru");
        ru.put("path", "messages_mall_trade_platform_ru.properties");
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
