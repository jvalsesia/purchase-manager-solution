CREATE TABLE IF NOT EXISTS purchase
(
    id               UUID           NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(255),
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_by       VARCHAR(255),
    description      VARCHAR(50)    NOT NULL,
    transaction_date date           NOT NULL,
    purchase_amount  DECIMAL(19, 2) NOT NULL,
    CONSTRAINT pk_purchase PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS exchange_rate
(
    id                 UUID           NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    created_by         VARCHAR(255),
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_by         VARCHAR(255),
    description        VARCHAR(50)    NOT NULL,
    transaction_date   date           NOT NULL,
    purchase_amountusd DECIMAL(19, 2) NOT NULL,
    exchange_rate      DECIMAL        NOT NULL,
    converted_amount   DECIMAL(19, 2) NOT NULL,
    CONSTRAINT pk_exchangerate PRIMARY KEY (id)
);


CREATE INDEX IF NOT EXISTS idx_purchase_transaction_date ON purchase(transaction_date);
CREATE INDEX IF NOT EXISTS idx_purchase_created_at ON purchase(created_at);

CREATE INDEX IF NOT EXISTS idx_exchange_rate_transaction_date ON exchange_rate(transaction_date);
CREATE INDEX IF NOT EXISTS idx_exchange_rate_created_at ON exchange_rate(created_at);
