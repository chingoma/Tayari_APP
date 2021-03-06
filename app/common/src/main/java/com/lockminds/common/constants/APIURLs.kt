package com.lockminds.libs.constants


class APIURLs{
    companion object {

        @JvmField val MEDIA_URL = "https://tayari.co.tz/uploads/media/"
        @JvmField val NEWS_MEDIA_URL = "https://tayari.co.tz/uploads/news_images/"
        @JvmField val SERVICES_MEDIA_URL = "https://tayari.co.tz/uploads/services_images/"
        @JvmField val PRODUCTS_MEDIA_URL = "https://tayari.co.tz/uploads/products_images/"

        @JvmField val BASE_URL = "https://api.tayari.com/api/"

        @JvmField val ALL_BUSINESS_URL = BASE_URL + "business/get_business"

        @JvmField val ALL_ORDERS_URL = BASE_URL + "orders/get_orders"

        @JvmField val ALL_PRODUCTS_URL = BASE_URL + "products/get_products"

        @JvmField val ALL_NEWS_URL = BASE_URL + "news/get_news"
        @JvmField val ALL_NEWS_CATEGORY_URL = BASE_URL + "news/get_categories"

        @JvmField val ALL_SERVICES_URL = BASE_URL + "services/get_services"
        @JvmField val ALL_SERVICES_CATEGORY_URL = BASE_URL + "services/get_categories"
        @JvmField val  ALL_CATEGORY_SERVICES_URL = BASE_URL + "services/get_category_services"
    }
}