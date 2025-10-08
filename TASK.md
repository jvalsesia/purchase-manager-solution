# Purchase Transaction Application Requirements

Your task is to build an application that supports the requirements outlined below. Outside of the requirements outlined, as well as any language limitations specified by the technical implementation notes below and/or by the hiring manager(s), the application is **your own design** from a technical perspective.

This is your opportunity to show us what you know! **Have fun, explore new ideas**, and as noted in the Questions section below, please let us know if you have any questions regarding the requirements!

## Requirements

### Requirement #1: Store a Purchase Transaction

Your application must be able to **accept and store** (i.e., persist) a purchase transaction with a **description**, **transaction date**, and a **purchase amount in United States dollars**. When the transaction is stored, it will be assigned a **unique identifier**.

#### Field Requirements
- **Description**: must not exceed **50 characters**
- **Transaction date**: must be a **valid date format**
- **Purchase amount**: must be a **valid positive amount rounded to the nearest cent**
- **Unique identifier**: must **uniquely identify** the purchase

### Requirement #2: Retrieve a Purchase Transaction in a Specified Country’s Currency

Based upon purchase transactions previously submitted and stored, your application must provide a way to **retrieve the stored purchase transactions converted to currencies** supported by the **[Treasury Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange)** based upon the **exchange rate active for the date of the purchase**.

The retrieved purchase should include the:
- **Identifier**
- **Description**
- **Transaction date**
- **Original US dollar purchase amount**
- **Exchange rate used**
- **Converted amount** based upon the specified currency’s exchange rate for the date of the purchase

#### Currency Conversion Requirements
- When converting between currencies, you do **not need an exact date match**, but must use a **currency conversion rate less than or equal to the purchase date** from **within the last 6 months**.
- If **no currency conversion rate is available** within 6 months equal to or before the purchase date, an **error should be returned** stating the purchase **cannot be converted to the target currency**.
- The **converted purchase amount to the target currency** should be **rounded to two decimal places** (i.e., cent).

## Technical Implementation

The technical implementation, including frameworks, libraries, etc., is **your own design** except for the **language**. The solution should be implemented in either **C#** or **Java**.

If you want to use a **JVM-based language other than Java**, please **gain permission to do so** in advance of implementing the solution.

You should build this application as if you are building an application to be **deployed in a Production environment**. This should be interpreted to mean that **all functional automated testing** you would include for a Production application should be expected. Please note that **non-functional test** (e.g., performance testing) automation is **not needed**.

Your application repository should be **fully functional** without installing separate:
- **Databases**
- **Web servers**
- **Servlet containers** (e.g., Jetty, Tomcat, etc.)

## Treasury Reporting Rates of Exchange Request Example
Query Params:
- **fields:** effective_date,country,currency,country_currency_desc,exchange_rate
- **sort:** -effective_date
- **filter:** country:eq:Brazil,effective_date:lte:2001-09-30
```sh
curl --location 'https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=effective_date%2Ccountry%2Ccurrency%2Ccountry_currency_desc%2Cexchange_rate&sort=-effective_date&filter=country%3Aeq%3ABrazil%2Ceffective_date%3Alte%3A2001-09-30'
```
