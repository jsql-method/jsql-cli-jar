function testRepo1() {

    jsql.repo()

        .namedQuery('someQuery', function () {

            return jsql.query("select * from user")
                .append("where query = test repo");
        });

    jsql.get("someQuery")

}

function testRepo2() {

    jsql.repo()

        .namedQuery('someQuery1', function () {

            return jsql.query("select * from user")
                .append("where query = test repo");
        })

        .namedQuery('someQuery2', function () {

            return jsql.query("select * from user")
                .append("where query = test repo");
        })

        .namedQuery('someQuery3', function () {

            return jsql.query("select * from user")
                .append("where query = test repo")
                .append("and id in (?,?,?)")
        });

    jsql.get("someQuery3")
}