var cases = {

    CREATE_WITH_NO_CONFIG: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        try {
            var jsql = new jSQL();
        } catch (error) {
            if (error == 'Error: jsql: no configuration') {
                status = 'SUCCESS';
            }
        }

        var end = new Date().getTime();
        var duration = end - start;

        callBack(status, duration);

    },

    CREATE_WITH_INVALID_HOST_1: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        try {
            var jsql = new jSQL({
                host: null
            });
        } catch (error) {
            if (error == 'Error: jsql: invalid host') {
                status = 'SUCCESS';
            }
        }

        var end = new Date().getTime();
        var duration = end - start;

        callBack(status, duration);

    },

    CREATE_WITH_INVALID_HOST_2: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        try {
            var jsql = new jSQL({
                host: ''
            });
        } catch (error) {
            if (error == 'Error: jsql: invalid host') {
                status = 'SUCCESS';
            }
        }

        var end = new Date().getTime();
        var duration = end - start;

        callBack(status, duration);

    },

    CREATE_WITH_INVALID_HOST_3: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        try {
            var jsql = new jSQL({
                host: new Date()
            });
        } catch (error) {
            if (error == 'Error: jsql: invalid host') {
                status = 'SUCCESS';
            }
        }

        var end = new Date().getTime();
        var duration = end - start;

        callBack(status, duration);

    },

    CREATE_WITH_INVALID_HOST_4: function (callBack) {

        var status = 'SUCCESS';
        var start = new Date().getTime();

        try {
            var jsql = new jSQL({
                host: 'http://localhost:8080'
            });
        } catch (error) {
            if (error == 'Error: jsql: invalid host') {
                status = 'FAILED';
            }
        }

        var end = new Date().getTime();
        var duration = end - start;

        callBack(status, duration);

    },

    INSERT_QUERY: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        var resultCallback = function () {
            var end = new Date().getTime();
            var duration = end - start;

            callBack(status, duration);
        }

        try {

            var jsql = new jSQL({
                host: 'http://localhost:8080',
                path: 'jsql'
            });

            //Przydałby się generator/repozytorium zapytań..
            //Przydała by się obsługa kilku na raz
            jsql.insert("insert into test_user (id, age, email, name) values (3, :age, :email, :name)")
                .params({
                    name: 'User1',
                    email: 'user1@email.com',
                    age: 35
                })
                .then(function (result) {
                    status = 'SUCCESS';
                })
                .catch(function (error) {
                })
                .test(function () {
                    resultCallback();
                });

        } catch (error) {
        }


    },


    SELECT_ASTERISK_QUERY: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        var resultCallback = function () {
            var end = new Date().getTime();
            var duration = end - start;

            callBack(status, duration);
        }

        try {

            var jsql = new jSQL({
                host: 'http://localhost:8080',
                path: 'jsql'
            });

            jsql.select("select * from test_user")
                .then(function (result) {

                    if(result.length > 0){
                        status = 'SUCCESS';
                    }

                })
                .catch(function (error) {
                })
                .test(function () {
                    resultCallback();
                });


        } catch (error) {
        }

    },

    SELECT_ONE_FIELD_QUERY: function (callBack) {

        var status = 'FAILED';
        var start = new Date().getTime();

        var resultCallback = function () {
            var end = new Date().getTime();
            var duration = end - start;

            callBack(status, duration);
        }

        try {

            var jsql = new jSQL({
                host: 'http://localhost:8080',
                path: 'jsql'
            });

            jsql.select("select a.name from test_user a")
                .then(function (result) {
                    if(result.length > 0){
                        status = 'SUCCESS';
                    }
                })
                .catch(function (error) {
                })
                .test(function () {
                    resultCallback();
                });

        } catch (error) {
        }


    }

}