package ggc.core;

interface NotificationMethod {
    void notifyPartnersNew(Product product, double price);

    void notifyPartnersBargain(Product product, double price);
}
