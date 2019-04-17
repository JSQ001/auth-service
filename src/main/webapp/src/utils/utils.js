import moment from 'moment';
import { parse, stringify } from 'qs';
import app from '../index';
import { formatMessage } from 'utils/locale';
import React from 'react';
import { Input, TimePicker, InputNumber, DatePicker, Select, Col, Form, Row } from 'antd';
import CustomAmount from 'components/Widget/custom-amount';
import Chooser from 'components/Widget/chooser';
import httpFetch from './fetch';
import config from '../config/config';
import SelectPlace from '../components/Widget/select-place';
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const FormItem = Form.item;

export function fixedZero(val) {
  return val * 1 < 10 ? `0${val}` : val;
}
// 保留任意位小数
export function toDecimal(number, precision = 2) {
  const temp = number;
  number = Number(number);
  // 判断是否为数字，不是数字抛出异常
  if (Number.isNaN(number)) {
    throw new Error('无效的数字:' + temp);
  }

  number = String(number);
  let numbers = number.split('.');
  // 如果不是小数，补充对应的0
  if (numbers.length === 1) {
    return number + '.' + '0'.repeat(precision);
  } else {
    // 如果是小数 但位数比要保留的位数少 也是要补0
    if (numbers[1].length < precision) {
      return number + '0'.repeat(precision - numbers[1].length);
    } else {
      // 取整数部分
      let intNumber = parseInt(number.split('.')[0]);
      // 取小数部分
      let decimal = number.split('.')[1];
      // 取出要保留的小数
      let frontDecimal = parseInt(decimal.substr(0, precision));
      // 去除要保留小数的后一位
      const lastDecimal = parseInt(decimal.substr(precision, 1));
      // 如果最后以为大于5 就进位
      if (lastDecimal >= 5) {
        frontDecimal += 1;
      }
      //判断小数部位是否需要进位 类似于19.996 保留2位小数 就是20.00
      if (frontDecimal === Math.pow(10, precision)) {
        frontDecimal = '0'.repeat(precision);
        intNumber += 1;
      }
      //处理 0.015 0.009 保留的小数中有0出现的情况
      frontDecimal = '0'.repeat(precision - String(frontDecimal).length) + frontDecimal;
      return `${intNumber}.${frontDecimal}`;
    }
  }
}

export function getTimeDistance(type) {
  const now = new Date();
  const oneDay = 1000 * 60 * 60 * 24;

  if (type === 'today') {
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);
    return [moment(now), moment(now.getTime() + (oneDay - 1000))];
  }

  if (type === 'week') {
    let day = now.getDay();
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);

    if (day === 0) {
      day = 6;
    } else {
      day -= 1;
    }

    const beginTime = now.getTime() - day * oneDay;

    return [moment(beginTime), moment(beginTime + (7 * oneDay - 1000))];
  }

  if (type === 'month') {
    const year = now.getFullYear();
    const month = now.getMonth();
    const nextDate = moment(now).add(1, 'months');
    const nextYear = nextDate.year();
    const nextMonth = nextDate.month();

    return [
      moment(`${year}-${fixedZero(month + 1)}-01 00:00:00`),
      moment(moment(`${nextYear}-${fixedZero(nextMonth + 1)}-01 00:00:00`).valueOf() - 1000),
    ];
  }

  if (type === 'year') {
    const year = now.getFullYear();

    return [moment(`${year}-01-01 00:00:00`), moment(`${year}-12-31 23:59:59`)];
  }
}

export function getPlainNode(nodeList, parentPath = '') {
  const arr = [];
  nodeList.forEach(node => {
    const item = node;
    item.path = `${parentPath}/${item.path || ''}`.replace(/\/+/g, '/');
    item.exact = true;
    if (item.children && !item.component) {
      arr.push(...getPlainNode(item.children, item.path));
    } else {
      if (item.children && item.component) {
        item.exact = false;
      }
      arr.push(item);
    }
  });
  return arr;
}

function accMul(arg1, arg2) {
  let m = 0;
  const s1 = arg1.toString();
  const s2 = arg2.toString();
  m += s1.split('.').length > 1 ? s1.split('.')[1].length : 0;
  m += s2.split('.').length > 1 ? s2.split('.')[1].length : 0;
  return (Number(s1.replace('.', '')) * Number(s2.replace('.', ''))) / 10 ** m;
}

export function digitUppercase(n) {
  const fraction = ['角', '分'];
  const digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
  const unit = [['元', '万', '亿'], ['', '拾', '佰', '仟', '万']];
  let num = Math.abs(n);
  let s = '';
  fraction.forEach((item, index) => {
    s += (digit[Math.floor(accMul(num, 10 * 10 ** index)) % 10] + item).replace(/零./, '');
  });
  s = s || '整';
  num = Math.floor(num);
  for (let i = 0; i < unit[0].length && num > 0; i += 1) {
    let p = '';
    for (let j = 0; j < unit[1].length && num > 0; j += 1) {
      p = digit[num % 10] + unit[1][j] + p;
      num = Math.floor(num / 10);
    }
    s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
  }

  return s
    .replace(/(零.)*零元/, '元')
    .replace(/(零.)+/g, '零')
    .replace(/^整$/, '零元整');
}

function getRelation(str1, str2) {
  if (str1 === str2) {
    console.warn('Two path are equal!'); // eslint-disable-line
  }
  const arr1 = str1.split('/');
  const arr2 = str2.split('/');
  if (arr2.every((item, index) => item === arr1[index])) {
    return 1;
  } else if (arr1.every((item, index) => item === arr2[index])) {
    return 2;
  }
  return 3;
}

function getRenderArr(routes) {
  let renderArr = [];
  renderArr.push(routes[0]);
  for (let i = 1; i < routes.length; i += 1) {
    // 去重
    // renderArr = renderArr.filter(item => getRelation(item, routes[i]) !== 1);
    // 是否包含
    //const isAdd = renderArr.every(item => getRelation(item, routes[i]) === 3);
    // if (isAdd) {
    renderArr.push(routes[i]);
    // }
  }
  return renderArr;
}

/**
 * Get router routing configuration
 * { path:{name,...param}}=>Array<{name,path ...param}>
 * @param {string} path
 * @param {routerData} routerData
 */
export function getRoutes(path, routerData) {
  let routes = Object.keys(routerData).filter(
    routePath => routePath.indexOf(path) === 0 && routePath !== path
  );

  // Replace path to '' eg. path='user' /user/name => name
  routes = routes.map(item => item.replace(path, ''));

  // Get the route to be rendered to remove the deep rendering
  const renderArr = getRenderArr(routes);

  // Conversion and stitching parameters
  const renderRoutes = renderArr.map(item => {
    const exact = !routes.some(route => route !== item && getRelation(route, item) === 1);

    return {
      exact,
      ...routerData[`${path}${item}`],
      key: `${path}${item}`,
      path: `${path}${item}`,
      level: (routerData[`${path}${item}`] && routerData[`${path}${item}`].level) || 1,
    };
  });

  renderRoutes.sort((x, y) => y.level - x.level);

  return renderRoutes;
}

export function getPageQuery() {
  return parse(window.location.href.split('?')[1]);
}

export function getQueryPath(path = '', query = {}) {
  const search = stringify(query);
  if (search.length) {
    return `${path}?${search}`;
  }
  return path;
}

/* eslint no-useless-escape:0 */
const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/;

export function isUrl(path) {
  return reg.test(path);
}

//节流函数
//一般用户输入框
export function superThrottle(fn, delay, mustRunDelay) {
  let timer = null;
  let t_start;
  return function() {
    let context = this;
    let args = arguments;
    let t_curr = +new Date();
    clearTimeout(timer);
    if (!t_start) {
      t_start = t_curr;
    }
    if (t_curr - t_start >= mustRunDelay) {
      fn.apply(context, args);
      t_start = t_curr;
    } else {
      timer = setTimeout(function() {
        fn.apply(context, args);
      }, delay);
    }
  };
}

export function messages(id, values = {}) {
  if (!id) return '';
  if (typeof id === 'object') {
    const result = formatMessage(id, values);
    return result;
  } else {
    const result = formatMessage({ id }, values);
    return result;
  }
}

export function getComponentById(id) {
  if (!app) return '';

  let components = app.getState().components.components;

  return getComponent(id, components);
}

function getComponent(id, components) {
  for (let i = 0, len = components.length; i < len; i++) {
    if (components[i].id == id) {
      return components[i];
    } else {
      let result = arguments.callee(id, components[i].children);
      if (result) return result;
    }
  }
}

//数组去重
export const myConcat = (origin, key = 'id') => {
  let temp = {};
  let target = [];
  origin.map(item => (temp[item[key]] = item));
  for (let item in temp) {
    target.push(temp[item]);
  }
  return target;
};

export const setDynamicValue = (fields, isLabel = true) => {
  const methods = [];
  fields.map(item => {
    item.value === '-' && (item.value = '');
    switch (item.fieldType) {
      case 'PARTICIPANTS':
      case 'PARTICIPANT':
        {
          methods.push(
            new Promise(resolve => {
              if (item.value) {
                httpFetch
                  .get(`${config.mdataUrl}/api/users/v2/${item.value}?roleType=TENANT`)
                  .then(res => {
                    item.defaultValue = isLabel
                      ? res.fullName
                      : [{ userOid: res.userOid, fullName: res.fullName }];
                    resolve(item);
                  });
              } else {
                item.defaultValue = isLabel ? '-' : [];
                resolve(item);
              }
            })
          );
        }
        break;
      case 'START_DATE_AND_END_DATE':
        {
          methods.push(
            new Promise(resolve => {
              if (item.expensePolicyFieldProperty) {
                if (isLabel) {
                  item.defaultValue = item.expensePolicyFieldProperty.dateTime1
                    ? moment(item.expensePolicyFieldProperty.dateTime1).format('YYYY-MM-DD')
                    : '-' + '~' + item.expensePolicyFieldProperty.dateTime2
                      ? moment(item.expensePolicyFieldProperty.dateTime2).format('YYYY-MM-DD')
                      : '-';
                  resolve(item);
                } else {
                  let defaultValue = [];
                  item.expensePolicyFieldProperty.dateTime1 &&
                    defaultValue.push(moment(item.expensePolicyFieldProperty.dateTime1));
                  item.expensePolicyFieldProperty.dateTime2 &&
                    defaultValue.push(moment(item.expensePolicyFieldProperty.dateTime2));
                  item.defaultValue = defaultValue;
                  resolve(item);
                }
              } else {
                resolve(item);
              }
            })
          );
        }
        break;
      case 'chooser':
        {
          // 仅费用政策
          let code;
          console.log(item.value);
          if (item.fieldId === '10001' || item.fieldId === '20001') {
            code = '1002';
            methods.push(
              new Promise(resolve => {
                if (item.value) {
                  httpFetch
                    .get(
                      `${
                        config.baseUrl
                      }/api/custom/enumeration/system/by/type/condition?systemCustomEnumerationType=${code}`
                    )
                    .then(res => {
                      if (isLabel) {
                        item.defaultValue = (res.find(i => i.id === item.value) || {}).name || '-';
                      } else {
                        console.log(res);
                        let value =
                          res.find(i => i.value === item.value || i.id === item.value) || {};
                        console.log(value);
                        item.defaultValue = [{ ...value, value: value.value, name: value.name }];
                      }
                      resolve(item);
                    });
                } else {
                  item.defaultValue = isLabel ? '-' : [];
                  resolve(item);
                }
              })
            );
          }
          if (item.fieldId === '10002' || item.fieldId === '20002') {
            code = '1008';
            methods.push(
              new Promise(resolve => {
                if (item.value) {
                  httpFetch
                    .get(
                      `${
                        config.baseUrl
                      }/api/custom/enumeration/system/by/type/condition?systemCustomEnumerationType=${code}`
                    )
                    .then(res => {
                      if (isLabel) {
                        item.defaultValue =
                          (res.find(i => i => i.value === item.value || i.id === item.value) || {})
                            .name || '-';
                      } else {
                        let value =
                          res.find(i => i.value === item.value || i.id === item.value) || {};
                        item.defaultValue = [{ value: value.value, name: value.name }];
                      }
                      resolve(item);
                    });
                } else {
                  item.defaultValue = isLabel ? '-' : [];
                  resolve(item);
                }
              })
            );
          }
          if (item.fieldId === '10003' || item.fieldId === '20003') {
            methods.push(
              new Promise(resolve => {
                if (item.value) {
                  httpFetch
                    .get(`${config.mdataUrl}/api/departments/${item.value}?roleType=TENANT`)
                    .then(res => {
                      item.defaultValue = isLabel
                        ? res.name
                        : [{ departmentOid: item.value, name: res.name }];
                      resolve(item);
                    });
                } else {
                  item.defaultValue = isLabel ? '-' : [];
                  resolve(item);
                }
              })
            );
          }
          if (item.fieldId === '30001') {
            methods.push(
              new Promise(resolve => {
                if (item.value) {
                  httpFetch.get(`${config.mdataUrl}/api/location/level/${item.value}`).then(res => {
                    item.defaultValue = isLabel ? res.name : [{ id: item.value, name: res.name }];
                    resolve(item);
                  });
                } else {
                  item.defaultValue = isLabel ? '-' : [];
                  resolve(item);
                }
              })
            );
          }
        }
        break;
      case 'DATE':
        {
          methods.push(
            new Promise(resolve => {
              item.defaultValue = isLabel
                ? item.value
                  ? moment(item.value).format('YYYY-MM-DD')
                  : '-'
                : item.value
                  ? moment(item.value)
                  : null;
              resolve(item);
            })
          );
        }
        break;
      case 'DATETIME':
        {
          methods.push(
            new Promise(resolve => {
              item.defaultValue = isLabel
                ? item.value
                  ? moment(item.value).format('YYYY-MM-DD:HH:MM:SSD')
                  : '-'
                : item.value
                  ? moment(item.value)
                  : null;
              resolve(item);
            })
          );
        }
        break;
      case 'LOCATION':
      case 'GPS':
        {
          methods.push(
            new Promise(resolve => {
              if (item.value) {
                httpFetch
                  .get(`${config.mdataUrl}/api/location/search/cities?id=${item.value}`)
                  .then(res => {
                    item.defaultValue = isLabel
                      ? res[0].description
                      : [{ key: item.value, label: res[0].description }];
                  });
                resolve(item);
              } else {
                item.defaultValue = isLabel ? '-' : [];
                resolve(item);
              }
            })
          );
        }
        break;
      default:
        methods.push(
          new Promise(resolve => {
            item.defaultValue = item.value;
            resolve(item);
          })
        );
    }
  });

  return new Promise(resolve => {
    Promise.all(methods).then(result => {
      resolve(result);
    });
  });
};

export const getFormItemDefaultValue = (field, value, isLabel) => {
  switch (field.fieldType) {
    case 'CUSTOM_ENUMERATION': {
      return ((field.options && field.options.map(item => item.value === value)) || {})[
        field.labelKey || 'label'
      ];
    }
    case 'PARTICIPANTS':
    case 'PARTICIPANT': {
      if (value && !field.fetched) {
        httpFetch.get(`${config.mdataUrl}/api/users/v2/${value}?roleType=TENANT`).then(res => {
          field.fetched = true;
          return 123;
        });
      }
    }
    case 'TEXT': {
      return value;
    }
    case 'DATE': {
      return isLabel ? moment(value).format('YYYY-MM-DD') : value ? moment(value) : null;
    }
    case 'START_DATE_AND_END_DATE': {
      //console.log(field)
      //console.log(value)
      if (isLabel) {
        value = '需后端返回值';
      } else {
        value = null;
      }
      return value;
    }
    case 'DATETIME': {
      return isLabel ? moment(value).format('YYYY-MM-DD:HH:MM:SS') : value ? moment(value) : null;
    }
    default:
      return '-';
  }
};

export const renderFormItem = (field, override = {}) => {
  //console.log(field)
  //console.log(override)

  switch (field.type) {
    case 'chooser':
      return <Chooser {...override} />;
    case 'PARTICIPANTS':
    case 'PARTICIPANT':
      return (
        <Chooser
          type="contract_user"
          single={true}
          labelKey="fullName"
          valueKey="id"
          {...override}
        />
      );

    case 'TEXT':
      return <Input {...override} placeholder={messages('common.please.enter')} />;
    case 'DOUBLE':
      return <CustomAmount {...override} />;
    case 'LONG':
      return <InputNumber precision={0} />;
    case 'POSITIVE_INTEGER':
      return <InputNumber min={0} precision={0} />;
    case 'DATETIME':
      return <TimePicker />;
    case 'DATE':
      return <DatePicker />;
    case 'START_DATE_AND_END_DATE':
      return (
        <RangePicker
          {...override}
          showTime={{ format: 'HH:mm' }}
          format="YYYY-MM-DD HH:mm"
          placeholder={['Start Time', 'End Time']}
        />
      );
    case 'MONTH':
      return <MonthPicker />;
    case 'CUSTOM_ENUMERATION':
      return (
        <Select>
          {field.options &&
            field.options.map(item => {
              return (
                <Select.Option key={item[field.valueKey || 'value']}>
                  {item[field.labelKey || 'label']}
                </Select.Option>
              );
            })}
        </Select>
      );
    case 'GPS':
    case 'LOCATION': {
      // console.log(field);
      // console.log(override);
      let options = [];
      let place = null;
      /*httpFetch.get(`${config.mdataUrl}/api/location/search/cities?description=${'上海'}`).then(res=>{
        //description
        console.log(res)
      })*/
      return <SelectPlace {...override} />;
    }
    default:
      return <InputNumber />;
  }

  const renderExpenseItem = (form, fields, rowAmount) => {
    const { getFieldDecorator } = form;
    const { formItemLayout, span } = fields;
    let col = fields.map(item => {
      return (
        <Col key={item.id} span={span || 6}>
          <FormItem {...formItemLayout} key={item.id} label={item.name}>
            {getFieldDecorator(item.id, {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.select'),
                },
              ],
              initialValue: getFormItemDefaultValue(item),
            })(renderFormItem({ type: item.fieldType }, override))}
          </FormItem>
        </Col>
      );
    });
    if (rowAmount) {
      let row = [];
      for (let i = 0, j = rowAmount; i < col.length; i = i + rowAmount, j = i + rowAmount) {
        row.push(col.slice(i, j));
      }
      return row.map(item => <Row key={new Date().getTime()}>{item}</Row>);
    }
    return col;
  };
};
