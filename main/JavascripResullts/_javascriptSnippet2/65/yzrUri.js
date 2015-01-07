function yzrUri(){}
yzrUri.prototype = new Yaezakura();
yzrUri.prototype.getQueryParameters = function() {

    // パラメータを定義するオブジェクト
    var parameters = {};

    // 変数を定義
    var match, plus, search, query;

    // +の正規表現を設定
    plus = /\+/g;

    // (key)=(value)の正規表現を設定
    search = /([^&=]+)=?([^&]*)/g;

    // クエリから?を除いて取得
    query  = window.location.search.substring(1);

    // 現在のインデックスから検索して、マッチしたら繰り返し
    while (match = search.exec(query)) {

        // 内容をparametersに設定
        parameters[decode(match[1])] = decode(match[2]);
    }

    // +を半角スペースに変更・URL文字列を通常の文字列に戻す
    function decode(str) {

        // 文字列を返す
        return decodeURIComponent(str.replace(plus, " "));
    };

    // パラメータを定義したオブジェクトを返す
    return parameters;
};
