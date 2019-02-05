function testAppend() {

	var myNewQuery = jsql.query("select * from user")
		.append("where id = :id")
		.append("and name = :name")

}