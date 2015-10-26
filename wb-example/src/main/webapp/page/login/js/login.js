
		document.onkeydown = function(e){
			if($(".bac").length==0)
			{
				if(!e) e = window.event;
				if((e.keyCode || e.which) == 13){
					var obtnLogin=document.getElementById("submit_btn")
					obtnLogin.focus();
				}
			}
		}

    	$(function(){
			//提交表单
			$('#submit_btn').click(function(){
				show_loading();
				var myReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/; //邮件正则
				if($('#username').val() == ''){
					show_err_msg('请输入用户名！');
					$('#username').focus();
				}else if($('#password').val() == ''){
					show_err_msg('请输入密码！');
					$('#password').focus();
				}else{

				}
			});
		});