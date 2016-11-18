define('app/jsp/home', function (require, exports, module) {
    'use strict';
    var $=require('jquery'),
        Widget = require('arale-widget/1.2.0/widget'),
        AjaxController = require('opt-ajax/1.0.0/index');
    require("jsviews/jsrender.min");
	require("zeroclipboard/ZeroClipboard.min");
    require("jquery-validation/1.15.1/jquery.validate");
    require("app/util/aiopt-validate-ext");
    var SendMessageUtil = require("app/util/sendMessage");

    //实例化AJAX控制处理对象
    var ajaxController = new AjaxController();

    var homePage = Widget.extend({
        //属性，使用时由类的构造函数传入
        attrs: {
            clickId:""
        },

        //事件代理
        events: {
            "click #recharge-popo":"_addOralOrderTemp",
            "click #toCreateOrder":"_toCreateOrder",
            "click #trante": "_mt",
            "click #playControl": "_text2audio",
			"click #humanTranBtn":"_goTextOrder"
        },

        //重写父类
        setup: function () {
            homePage.superclass.setup.call(this);
			// 定义一个新的复制对象
			var clip = new ZeroClipboard( document.getElementById("copyText"), {
				moviePath: "ZeroClipboard.swf"
			});

			// 复制内容到剪贴板成功后的操作
			clip.on( 'complete', function(client, args) {
				// alert("复制成功，复制内容为："+ args.text);
			} );
        },
		//人工翻译,跳转到笔译订单
       	_goTextOrder:function(){
			window.location.href=_base+"/written";
		},

        //翻译
        _mt:function() {
        	var from = $(".dropdown .selected").eq(0).attr("value");
        	var to = $(".dropdown .selected").eq(1).attr("value");
			if (Window.console){
				console.log("from:"+from+",to:"+to);
			}
        	ajaxController.ajax({
				type: "post",
				url: _base + "/mt",
				data: {
					from: from,
					to: to,
					text: $("#int-before").val()
				},
				success: function (data) {
					$("#transRes").val(data.data.text);

					//翻译后的文字超过1000，隐藏播放喇叭
					if ($("#transRes").val().length > 1000)
						$("#playControl").hide();
					else
						$("#playControl").show();
				}
			});
        },
        
        //文本转音频
        _text2audio:function() {
			//获取目标语言编码
			var to =$(".dropdown .selected").eq(1).attr("value");

        	var myAudio = document.getElementById('audioPlay');
	    	if(myAudio.paused){
		        var itostr = $.trim($("#transRes").val());
		        if(itostr != null){
	        		 var playUrl = _base + '/Hcicloud/text2audio?lan='+to+'&text='+itostr;
	        		
	        		 $("#audioPlay").attr("src", playUrl);
	        		 console.log(playUrl);
	        		 myAudio.play();
		        }
	        }else{
	            myAudio.pause();
	        }
        },
        
        
    });

    module.exports = homePage;
});
