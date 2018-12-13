 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentId;//赋予上级ID
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	/*1.组装选中的id
	2.调用后台的删除方法，把ids穿过去，执行删除操作*/
	//组装选中id
	$scope.selectIds=[];
	//用户勾选的复选框
	//$event 表示 源，相当于把input本身整个传递过来
	$scope.selection=function($event,id){
		if($event.target.checked){
			//把id放在selectIds数组里
			$scope.selectIds.push(id);
			console.log($scope.selectIds)
		} else {
			var idx = $scope.selectIds.indexOf(id);
	         $scope.selectIds.splice(idx, 1);//删除 
	         console.log($scope.selectIds)
		}
	}
	
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);//重新加载
					//$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.parentId=0;//上级ID
	$scope.findByParentId=function(parentId){
		$scope.parentId=parentId;//记住上级ID
		itemCatService.findByParentId(parentId).success(
			function(resp){
				$scope.list = resp;
			}	
		)
	}
	
	$scope.grade=1;//默认为1级	
	//设置级别
	$scope.setGrade=function(value){
		$scope.grade=value;
	}
	//读取列表
	$scope.selectList=function(p_entity){			
		if($scope.grade==1){//如果为1级
			$scope.entity_1=null;	
			$scope.entity_2=null;
		}		
		if($scope.grade==2){//如果为2级
			$scope.entity_1=p_entity;	
			$scope.entity_2=null;
		}		
		if($scope.grade==3){//如果为3级
			$scope.entity_2=p_entity;		
		}		
		$scope.findByParentId(p_entity.id);	//查询此级下级列表
	}
    
});	
