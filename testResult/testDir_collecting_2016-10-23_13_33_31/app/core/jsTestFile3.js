function testFunction1() {

	jsql.delete("delete from test_user where id = :id")
		.then(function (result) {
		})
		.catch(function (error) {
		})
		.test(function () {
		});

}


function testFunction2() {

	jsql.select("select * from test_address where test_user_id = ?")
		.then(function (result) {
		})
		.catch(function (error) {
		})
		.test(function () {
		});

}