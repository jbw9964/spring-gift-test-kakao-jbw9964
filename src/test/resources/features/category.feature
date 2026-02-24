Feature: 카테고리 관리
  사용자가 카테고리를 추가하고 조회할 수 있다.

  Scenario: 사용자가 카테고리를 추가한다
    When 카테고리 "Category 1"를 추가한다
    Then 상태 코드는 200이다
    And 응답의 "id"는 비어있지 않다
    And 응답의 "name"은 "Category 1"이다

  Scenario: 사용자가 카테고리를 조회한다
    Given 카테고리 "Category 0"가 이미 존재한다
    When 카테고리 목록을 조회한다
    Then 상태 코드는 200이다
    And 응답 목록의 크기는 1이다
    And 응답 목록의 "name"에 "Category 0"가 포함되어 있다

  Scenario: 사용자가 카테고리를 추가하고 조회한다
    Given 카테고리 "Category 0"가 이미 존재한다
    When 카테고리 "Category 2"를 추가한다
    Then 상태 코드는 200이다
    When 카테고리 목록을 조회한다
    Then 응답 목록의 크기는 1 이상이다
    And 응답 목록의 "name"에 "Category 2"가 포함되어 있다

  Scenario: 카테고리가 없으면 빈 목록이 조회된다
    When 카테고리 목록을 조회한다
    Then 상태 코드는 200이다
    And 응답 목록의 크기는 0이다
