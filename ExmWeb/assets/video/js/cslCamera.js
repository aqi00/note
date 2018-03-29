$.Pgater = (function() {
	var agent = navigator.userAgent.toLowerCase();
	var iswx = agent.indexOf('qqbrowser') >= 0;
	if (iswx) {
		var File = $(
			"<input type='file' id='csl_gater_file' accept='video/*' capture='camera' multiple='multiple'>"
		);
		// var File=$("<input type='file' id='csl_gater_file' accept='video/*' capture='camcorder' multiple='multiple'>");
	} else {
		var File = $(
			"<input type='file' id='csl_gater_file' accept='video/*' multiple='multiple'>"
		);
		// var File=$("<input type='file' id='csl_gater_file' accept='video/*' multiple='multiple'>");
	}
	File.css('display', 'none');
	return function(target, callBack) {
		console.log(File);
		this.ele = File;
		this.parent = target;
		this.parent.append(this.ele);
		this.bindClk(this.parent, this.ele[0]);
		this.bindFuc(this.ele, callBack);
	};
})();
$.Pgater.prototype.bindFuc1 = function(ele, callBack) {
	ele.on('change', function() {
		var all = ele[0].files;
		var reader = new FileReader();//
		var album = [];
		var length = all.length;
		var i = 0;
		var recur = function() {
			reader.readAsDataURL(all[i]);//
			var One = all[i];
			reader.onload = function(e) {
				//alert(One);
				console.log(One);
				One.data = this.result;
				album.push(One);
				i++;
				if (i < length) {
					recur();
				} else {
					ele.value = '';
					//alert(i);
					callBack(album, img);
				}
			};
		};
		recur();
	});
};
$.Pgater.prototype.bindFuc = function(ele, callBack) {
	ele.on('change', function() {
		window.URL = window.URL || window.webkitURL;
		var all = ele[0].files;
		var album = [];
		var length = all.length;
		var i = 0;
		var recur = function() {
			var blob = new Blob([all[i]]), // 文件转化成二进制文件
			url = URL.createObjectURL(blob); //转化成url
			var One = all[i];
				One.data = url;
				album.push(One);
				i++;
				if (i < length) {
					recur();
				} else {
					ele.value = '';
					callBack(album, img);
				}
		};
		recur();
	});
};
$.Pgater.prototype.bindClk = function(ele, tar) {
	ele.on('click', function() {
		tar.click();
	});
};
