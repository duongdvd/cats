function editUser() {
	var firstName = $('#firstName').val().trim();

	var form_data = $('#add_company')[0];
	var data = new FormData(form_data);

	$.post("/editSysadmin/editUserAdmin", {
		firstName: firstName
	}, function (data, status) {
		if (data == "true") {
			window.location.href = "/company";
		}
	});
};

function submitForm(){
	var pass1 = $('#newPasswd').val().trim();
	var pass2 = $('#confirmPasswd').val().trim();
	alert(pass1);
	if(mobilenumber()){
		$('#myForm').submit();
	}
	else{
		alert("Please Input Correct Mobile Numbers");
	}
}