describe('Adding a new task named "my task"', function() {

  var scope;
  var ctrl;

  beforeEach(module('todoApp'));

  beforeEach(inject(function($rootScope, $controller) {
    scope = $rootScope.$new();
    ctrl = $controller('TodoListController', {$scope: scope});
    ctrl.todos = [];
  }));

  it('should add a new item in the list', function() {
    ctrl.addTodo();
    expect(ctrl.todos.length).to.equal(1);
  })

  it('should add a new item called "my task" at the end of the list', function() {
    ctrl.todoText = "my task";
    ctrl.addTodo();
    expect(ctrl.todos[0].text).to.equal("my task");
  })

  it('should add a new unchecked item at the end of the list', function() {
    ctrl.addTodo();
    expect(ctrl.todos[0].done).to.equal(false);
  })

  it('should increase the remaining task count', function() {
    ctrl.addTodo();
    expect(ctrl.remaining()).to.equal(1);
  })
});

describe('Checking a task', function() {
  
    var scope;
    var ctrl;
  
    beforeEach(module('todoApp'));
  
    beforeEach(inject(function($rootScope, $controller) {
      scope = $rootScope.$new();
      ctrl = $controller('TodoListController', {$scope: scope});
      ctrl.todos = [
        {text:'learn AngularJS', done:true},
        {text:'build an AngularJS app', done:false}];
      }));
  
    it('should decrease the remaining task count', function() {
      ctrl.todos[1].done = true;
      expect(ctrl.remaining()).to.equal(0);
    })
  });
  