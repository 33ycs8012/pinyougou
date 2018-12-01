 //品牌控制层 
app.controller('baseController' ,function($scope){	
	

    
	//分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法 
	$scope.paginationConf = {
		currentPage: 1,
		totalItems: 10,
		itemsPerPage: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
			$scope.reloadList();
		}
	};
	
    //重新加载列表 数据
	$scope.reloadList=function(){
		$scope.search( $scope.paginationConf.currentPage ,  $scope.paginationConf.itemsPerPage );
	}
	//用户勾选的ID集合
	$scope.selectIds=[]; 
	//用户勾选复选框 
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
			//push向集合添加元素
			$scope.selectIds.push(id); 					
		}else{
			//查找值的 位置
			var index= $scope.selectIds.indexOf(id);
			
			//参数1：移除的位置 参数2：移除的个数
			$scope.selectIds.splice(index,1);  
		}
	}
	
	//页面的品牌和规格是用的json格式的字符串存储在DB中的,所以我们需要这么一个转换的过程,好让数据以用户可识别的样式展现出来
	$scope.jsonToString=function(jsonString,key){
		
		var json= JSON.parse(jsonString);
		var value="";
		
		for(var i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}			
			value +=json[i][key];			
		}
				
		return value;
	}
	
});	