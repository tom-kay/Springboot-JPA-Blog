let index = {
	init: function(){
		$("#btn-save").on("click", () => {
			this.save();
		});
		$("#btn-delete").on("click", () => {
      this.deleteById();
    });
		$("#btn-update").on("click", () => {
      this.update();
    });
	},
	
	save: function (){
		//alert('user의 save함수 호출됨');
		let data = {
			title: $("#title").val(),
			content: $("#content").val()
		};
		
		$.ajax({
			type:"POST",
			url:"/api/board",
			data: JSON.stringify(data),	//http body데이터
			contentType: "application/json; charset=utf-8",	//body 데이터가 어떤 타입인지
			dataType: "json"	//응답
			
		}).done(function(resp){
			alert("글쓰기가 완료되었습니다");
			console.log(resp);
			location.href="/";
		}).fail(function(error){
			alert(JSON.stringify(error));
		});
	},
	
	deleteById: function (){
	  let id = $("#id").text();
	  
	  
    $.ajax({
      type:"DELETE",
      url:"/api/board/"+id,
      dataType: "json"  //응답
      
    }).done(function(resp){
      alert("삭제가 완료되었습니다");
      console.log(resp);
      location.href="/";
    }).fail(function(error){
      alert(JSON.stringify(error));
    });
  },
	
	update: function (){
    let id = $("#id").val();
    
    let data = {
      title: $("#title").val(),
      content: $("#content").val()
    };
    
    $.ajax({
      type:"PUT",
      url:"/api/board/"+id,
      data: JSON.stringify(data), //http body데이터
      contentType: "application/json; charset=utf-8", //body 데이터가 어떤 타입인지
      dataType: "json"  //응답
      
    }).done(function(resp){
      alert("글수정이 완료되었습니다");
      console.log(resp);
      location.href="/";
    }).fail(function(error){
      alert(JSON.stringify(error));
    });
  },
};

index.init();