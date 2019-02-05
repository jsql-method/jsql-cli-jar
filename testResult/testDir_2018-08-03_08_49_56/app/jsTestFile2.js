function testFunction1() {

	jsql.insert("insert into test_user values(?,?,?,?)")
		.then(function (result) {
		})
		.catch(function (error) {
		})
		.test(function () {
		});

}

function testFunction2() {

	jsql.update("update test_address set post_code = ? where id = :id")
		.then(function (result) {
		})
		.catch(function (error) {
		})
		.test(function () {
		});

}