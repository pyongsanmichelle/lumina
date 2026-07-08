Feature: Users API 機能テスト

  Background:
    * url 'http://localhost:8081/api/v1'
    * header Accept = 'application/json'
    * header Content-Type = 'application/json'

  Scenario: ユーザー一覧取得（検索）
    Given path 'users'
    And param name = 'Test'
    And param email = 'test@example.com'
    When method get
    Then status 200
    And match response == '#[]'

  Scenario: ユーザー個別取得（存在しないID）
    Given path 'users', 999999
    When method get
    Then status 404

  Scenario: ユーザー登録（正常系）
    * def newUser =
    """
    {
      "idpSubject": "test-subject-001",
      "email": "testuser@example.com",
      "name": "Test User",
      "timezone": "Asia/Tokyo"
    }
    """
    Given path 'users'
    And request newUser
    When method post
    Then status 201
    And match response.id == '#number'
    And match response.idpSubject == 'test-subject-001'
    And match response.email == 'testuser@example.com'
    And match response.name == 'Test User'
    And match response.timezone == 'Asia/Tokyo'
    And match response.status == 'ENABLED'
    And match response.version == 0
    And match response.createdBy != null
    And match response.createdAt != null
    And match response.updatedBy == response.createdBy
    And match response.createdAt == '#string'
    And match response.updatedAt == '#string'
    * def createdUserId = response.id

  Scenario: ユーザー登録（必須項目欠落）
    * def invalidUser =
    """
    {
      "email": "test@example.com"
    }
    """
    Given path 'users'
    And request invalidUser
    When method post
    Then status 400
    And match response.globalErrors != null
    And match response.fieldErrors contains
    """
    {
      field: 'idpSubject',
      rejectedValue: null,
      message: 'IdP user ID is required.'
    }
    """
    And match response.fieldErrors contains
    """
    {
      field: 'name',
      rejectedValue: null,
      message: 'User name is required.'
    }
    """

  Scenario: ユーザー登録（メール形式不正）
    * def invalidEmailUser =
    """
    {
      "idpSubject": "test-subject-002",
      "email": "invalid-email",
      "name": "Test User 2",
      "timezone": "Asia/Tokyo"
    }
    """
    Given path 'users'
    And request invalidEmailUser
    When method post
    Then status 400
    And match response.fieldErrors contains
    """
    {
      field: 'email',
      rejectedValue: 'invalid-email',
      message: 'Invalid email address format.'
    }
    """

  Scenario: ユーザー登録（重複ID）
    * def duplicateUser =
    """
    {
      "idpSubject": "test-subject-001",
      "email": "duplicate@example.com",
      "name": "Duplicate User",
      "timezone": "Asia/Tokyo"
    }
    """
    Given path 'users'
    And request duplicateUser
    When method post
    Then status 409
    And match response.globalErrors != null

  Scenario: ユーザー個別取得（正常系）
    Given path 'users', 1
    When method get
    Then status 200
    And match response.id == 1
    And match response.idpSubject != null
    And match response.email != null
    And match response.name != null
    And match response.timezone != null
    And match response.status != null
    And match response.version != null

  Scenario: ユーザー更新（正常系）
    * def updateUser =
    """
    {
      "name": "Updated User",
      "timezone": "America/New_York",
      "version": 0
    }
    """
    Given path 'users', 1
    And request updateUser
    And header If-Match = '0'
    When method put
    Then status 200
    And match response.name == 'Updated User'
    And match response.timezone == 'America/New_York' 

  Scenario: ユーザー更新（楽観ロックエラー）
    * def updateUserOldVersion =
    """
    {
      "name": "Old Version User",
      "timezone": "Asia/Tokyo",
      "version": 0
    }
    """
    Given path 'users', 1
    And request updateUserOldVersion
    And header If-Match = '0'
    When method put
    Then status 412
    And match response.globalErrors != null
    And match response.globalErrors[0].code == 'OptimisticLockException'

  Scenario: ユーザー更新（存在しないID）
    * def updateNonExistentUser =
    """
    {
      "name": "Non Existent User",
      "timezone": "Asia/Tokyo",
      "version": 0
    }
    """
    Given path 'users', 999999
    And request updateNonExistentUser
    And header If-Match = '0'
    When method put
    Then status 404

  Scenario: ユーザー削除（正常系）
    Given path 'users', 2
    When method delete
    Then status 204

  Scenario: ユーザー削除（存在しないID）
    Given path 'users', 999999
    When method delete
    Then status 404

  Scenario: ユーザー検索（名前で部分一致）
    Given path 'users'
    And param name = 'Test'
    When method get
    Then status 200
    And match response != null

  Scenario: ユーザー検索（メールで完全一致）
    Given path 'users'
    And param email = 'test@example.com'
    When method get
    Then status 200
    And match response != null