#noinspection NonAsciiCharacters
Feature: 선물하기
  사용자가 다른 사용자에게 선물을 보낼 수 있다.

  Background:
    Given 회원 "Gift Sender"가 이미 존재한다
    And 회원 "Gift Receiver"가 이미 존재한다
    And 카테고리 "Category 0"가 이미 존재한다
    And 상품 "Product 0"가 가격 1000으로 이미 존재한다
    And 상품의 옵션 "Option 0"이 재고 100으로 이미 존재한다

  Scenario: 사용자가 선물을 보낸다
    When "Gift Sender"가 "Gift Receiver"에게 옵션 "Option 0" 100개를 선물한다
    Then 상태 코드는 200이다

  Scenario: 선물 요청 시 옵션 id가 제공되지 않으면 에러가 발생한다
    When 옵션 id 없이 선물 보내기를 요청한다
    Then 상태 코드는 400이다

  Scenario: 선물 요청 시 존재하지 않는 옵션 id가 제공되면 에러가 발생한다
    When 존재하지 않는 옵션 id로 선물 보내기를 요청한다
    Then 상태 코드는 404이다

  Scenario: 선물 요청 시 수신자 id가 제공되지 않으면 에러가 발생한다
    When 수신자 id 없이 선물 보내기를 요청한다
    Then 상태 코드는 400이다

  Scenario: 선물 요청 시 존재하지 않는 수신자 id가 제공되면 에러가 발생한다
    When 존재하지 않는 수신자 id로 선물 보내기를 요청한다
    Then 상태 코드는 404이다

  Scenario: 선물 요청 시 Member-Id 헤더가 누락되면 에러가 발생한다
    When Member-Id 헤더 없이 선물 보내기를 요청한다
    Then 상태 코드는 400이다

  Scenario: 선물 요청 시 존재하지 않는 Member-Id가 제공되면 에러가 발생한다
    When 존재하지 않는 Member-Id로 선물 보내기를 요청한다
    Then 상태 코드는 404이다

  Scenario Outline: 재고가 <stock>개일 때 <quantity>개를 선물하면 <result>
    Given 상품의 옵션 "경계값 옵션"이 재고 <stock>으로 이미 존재한다
    When "Gift Sender"가 "Gift Receiver"에게 옵션 "경계값 옵션" <quantity>개를 선물한다
    Then 상태 코드는 <statusCode>이다

    Examples:
      | stock | quantity | statusCode | result |
      | 10    | 9        | 200        | 성공한다   |
      | 10    | 10       | 200        | 성공한다   |
      | 10    | 11       | 500        | 실패한다   |
      | 0     | 1        | 500        | 실패한다   |
