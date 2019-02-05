function testRepo1() {

    jsql.repo()

        .namedQuery('someQuery', function () {

            return jsql.query("iBGV1cMhDsQmK1rxUMtQ")
                .append("47Fa7i8yneUUkzidM2eRA");
        });

    jsql.get("someQuery")

}

function testRepo2() {

    jsql.repo()

        .namedQuery('someQuery1', function () {

            return jsql.query("iBGV1cMhDsQmK1rxUMtQ")
                .append("47Fa7i8yneUUkzidM2eRA");
        })

        .namedQuery('someQuery2', function () {

            return jsql.query("iBGV1cMhDsQmK1rxUMtQ")
                .append("47Fa7i8yneUUkzidM2eRA");
        })

        .namedQuery('someQuery3', function () {

            return jsql.query("iBGV1cMhDsQmK1rxUMtQ")
                .append("47Fa7i8yneUUkzidM2eRA")
                .append("3y55PAlyRbYyubgkmVbA")
        });

    jsql.get("someQuery3")
}