/**
 ** 加法函数，用来得到精确的加法结果
 ** 说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
 ** 调用：accAdd(arg1,arg2)
 ** 返回值：arg1加上arg2的精确结果
 **/
function accAdd(arg1, arg2) {
  var r1, r2, m, c;
  try {
    r1 = arg1.toString().split('.')[1].length;
  } catch (e) {
    r1 = 0;
  }
  try {
    r2 = arg2.toString().split('.')[1].length;
  } catch (e) {
    r2 = 0;
  }
  c = Math.abs(r1 - r2);
  m = Math.pow(10, Math.max(r1, r2));
  if (c > 0) {
    var cm = Math.pow(10, c);
    if (r1 > r2) {
      arg1 = Number(arg1.toString().replace('.', ''));
      arg2 = Number(arg2.toString().replace('.', '')) * cm;
    } else {
      arg1 = Number(arg1.toString().replace('.', '')) * cm;
      arg2 = Number(arg2.toString().replace('.', ''));
    }
  } else {
    arg1 = Number(arg1.toString().replace('.', ''));
    arg2 = Number(arg2.toString().replace('.', ''));
  }
  return (arg1 + arg2) / m;
}

/**
 * 判断两个对象是否相等
 * @param {*} x
 * @param {*} y
 */

function objectEquals(x, y) {
  var f1 = x instanceof Object;
  var f2 = y instanceof Object;
  if (!f1 || !f2) {
    return x === y;
  }
  if (Object.keys(x).length !== Object.keys(y).length) {
    return false;
  }
  var newX = Object.keys(x);
  for (var p in newX) {
    p = newX[p];
    var a = x[p] instanceof Object;
    var b = y[p] instanceof Object;
    if (a && b) {
      objectEquals(x[p], y[p]);
    } else if (x[p] != y[p]) {
      return false;
    }
  }
  return true;
}

/**
 * 等分分割数组
 * @param {*} array
 * @param {*} size
 */
function sliceArray(array, size) {
  var result = [];
  for (var x = 0; x < Math.ceil(array.length / size); x++) {
    var start = x * size;
    var end = start + size;
    result.push(array.slice(start, end));
  }
  return result;
}

/**
 * 获取对象数组中某个属性相等的对象
 * @param {*属性值名} key
 * @param {*要相等的数据} value
 * @param {*数组} data
 */
function selectDataByKey(key, value, data) {
  if (key && value && data) {
    let temp = data.filter(item => {
      return item[key] === value;
    });
    return temp[0];
  } else {
    return {};
  }
}

export { accAdd, objectEquals, sliceArray, selectDataByKey };
