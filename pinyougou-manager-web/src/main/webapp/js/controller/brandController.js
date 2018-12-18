app.controller('brandController', function($scope, $controller, brandService) {
	// 继承,伪继承,是通过传递scope来实现的,让两个controller的scope通用
	$controller('baseController', {
		$scope : $scope
	});
	// 查询品牌列表
	$scope.findAll = function() {
		brandService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页控件配置currentPage:当前页 totalItems :总记录数 itemsPerPage:每页记录数 perPageOptions
	// :分页选项 onChange:当页码变更后自动触发的方法
	/*
	 * $scope.paginationConf = { currentPage: 1, totalItems: 10, itemsPerPage:
	 * 10, perPageOptions: [10, 20, 30, 40, 50], onChange: function(){
	 * $scope.reloadList(); } };
	 */

	// 刷新列表
	/*
	 * $scope.reloadList=function(){ $scope.findPage(
	 * $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage ); }
	 */
	// 刷新列表
	/*
	 * $scope.reloadList=function(){ $scope.search(
	 * $scope.paginationConf.currentPage , $scope.paginationConf.itemsPerPage ); }
	 */

	// 分页
	$scope.findPage = function(page, size) {
		brandService.findPage(page, size).success(function(response) {
			$scope.list = response.rows;// 显示当前页数据
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 新增
	$scope.save = function() {
		var object = null;// 方法名
		if ($scope.entity.id != null) {
			object = brandService.update($scope.entity);
		} else {
			object = brandService.add($scope.entity);
		}
		object.success(function(response) {
			if (response.success) {
				$scope.reloadList();// 刷新
			} else {
				alert(response.message);
			}
		});
	}

	// 查询实体
	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}

	/*
	 * //用户勾选的ID集合 $scope.selectIds=[]; //用户勾选复选框
	 * $scope.updateSelection=function($event,id){ if($event.target.checked){
	 * //push向集合添加元素 $scope.selectIds.push(id); }else{ //查找值的 位置 var index=
	 * $scope.selectIds.indexOf(id);
	 * 
	 * //参数1：移除的位置 参数2：移除的个数 $scope.selectIds.splice(index,1); } }
	 */

	// 删除
	$scope.dele = function() {
		if (confirm('确定要删除吗？')) {
			brandService.dele($scope.selectIds).success(function(response) {
				if (response.success) {
					$scope.reloadList();// 刷新
				} else {
					alert(response.message);
				}
			});
		}
	}

	$scope.searchEntity = {};
	// 条件查询
	$scope.search = function(page, size) {
		brandService.search(page, size, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;// 显示当前页数据
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	};
	//规格模板品牌下拉回显
	$scope.brandList={data:[]};
	$scope.findBrandList = function(){
		brandService.selectOptionList().success(
				function (res) {
					brandList={data:res};
				}
		)
	};


});