import React from 'react';
import { connect } from 'dva';
import { Table, Input, Form } from 'antd';
import Lov from 'widget/Template/lov';
import { sliceArray } from './utils';

const FormItem = Form.Item;
class FundEditableTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loadding: false,
      copyDataSource: [],
      copyColumns: [],
      copyPagination: {},
      count: 0, // 数量
    };
  }

  componentDidMount() {
    const { columns, pagination, onRef } = this.props;
    onRef(this);
    this.getCopyColumns(columns);
    this.setState({
      copyPagination: pagination,
    });
  }

  componentWillReceiveProps(nextProps) {
    const { copyDataSource } = this.state;
    const { dataSource } = nextProps;
    // 防止form表单触发componentWillReceiveProps改变table的dataSource
    if (copyDataSource.length === 0) {
      this.getCopyDataSource(dataSource);
    }
  }

  /**
   * 对dataSource处理
   */
  getCopyDataSource = dataSource => {
    this.setState({
      loadding: true,
    });
    const copyDataSource = dataSource.map((item, index) => {
      return {
        ...item,
        key: index,
      };
    });
    this.setState({
      copyDataSource,
      count: dataSource.length,
      loadding: true,
    });
    this.setState({
      loadding: false,
    });
    return copyDataSource;
  };

  /**
   * 对columns处理
   */
  getCopyColumns = columns => {
    const copyColumns = columns.map(col => {
      const {
        form: { getFieldDecorator },
      } = this.props;
      // 区分配置项是否可编辑
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        render: (text, record, index) => {
          return (
            <FormItem style={{ marginBottom: 0 }}>
              {getFieldDecorator(`${index}-${col.dataIndex}`, {
                rules: [{ required: true, message: '请输入' }],
                initialValue: text,
              })(this.renderFormItem(col))}
            </FormItem>
          );
        },
      };
    });

    this.setState({
      copyColumns,
    });
    return copyColumns;
  };

  renderFormItem = item => {
    switch (item.type) {
      case 'input': {
        return <Input />;
      }
      case 'lov': {
        return (
          <Lov
            code={item.lovCode}
            valueKey={item.lovValueKey}
            lableKey={item.lovlabelKey}
            extraParams={item.lovSetOfBooksId}
            single={item.single}
          />
        );
      }
      default: {
        return <Input />;
      }
    }
  };

  /**
   * 添加行
   */
  handleAdd = () => {
    const { copyDataSource, count } = this.state;
    const newData = {
      key: count + 1,
      paramsCode: '',
      paramsDesc: '',
      paramsValue: '',
    };
    this.setState({
      copyDataSource: [...copyDataSource, newData],
      count: count + 1,
    });
  };

  /**
   * 保存
   */
  saveTable = () => {
    const { form, saveTable } = this.props;
    const { copyColumns } = this.state;
    form.validateFields((err, value) => {
      if (!err) {
        const saveValue = this.dealData(value, copyColumns.length);
        saveTable(saveValue);
      }
    });
  };

  /**
   * 处理数据
   */
  dealData = (data, length) => {
    const result = [];
    const resultArr = []; // 创建一个数组
    /**
     * 将对象
      {
        0-paramsCode: "code01"
        0-paramsDesc: "测试COD1212E01sss"
        0-paramsValue: "测试参数值011111"
        1-paramsCode: "wwww"
        1-paramsDesc: "2334"
        1-paramsValue: "44444"
        2-paramsCode: "ssss"
        2-paramsDesc: "sssssss"
        2-paramsValue: "ssssss"
      }
    * 变为对象数组[{name:paramsCode,value:code01},{name:paramsDesc,value:测试COD1212E01sss}]
    */
    for (const key in data) {
      if (Object.prototype.hasOwnProperty.call(data, key)) {
        const temp = {};
        // eslint-disable-next-line
        temp.name = key.split('-')[1];
        temp.value = data[key];
        resultArr.push(temp);
      }
    }
    // 将对象数组等份分割
    const sliceArrayResult = sliceArray(Object.values(resultArr), length);
    /* 将分割后的数组进行遍历重组变为
      [
        {name:value,name:value},
        {name:value,name:value}
      ]
    */
    sliceArrayResult.forEach(item => {
      const tempObj = {};
      item.forEach(otherItem => {
        tempObj[otherItem.name] = otherItem.value;
      });
      result.push(tempObj);
    });
    return result;
  };

  render() {
    const { copyDataSource, copyColumns, copyPagination, loadding } = this.state;
    return (
      <div>
        <Form>
          <Table
            rowClassName={() => 'editable-row'}
            loadding={loadding}
            bordered
            dataSource={copyDataSource}
            columns={copyColumns}
            pagination={copyPagination}
          />
        </Form>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(FundEditableTable));
