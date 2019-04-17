import React from 'react';
import { message, Button, Menu, Icon, Dropdown } from 'antd';
import config from 'config';
import httpFetch from 'share/httpFetch';
import PropTypes from 'prop-types';

class SelectType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuList: [],
    };
  }

  componentDidMount() {
    this.getMenuList();
  }

  // 获取添加发票按钮下拉框
  getMenuList = () => {
    httpFetch
      .get(`${config.expenseUrl}/api/invoice/type/sob/tenant/query`)
      .then(res => this.setState({ menuList: res.data }))
      .catch(err => message.error(err.response.data.message));
  };

  handleClick = value => {
    this.props.onOk({
      id: value.key,
      name: value.item.props.children,
    });
  };

  render() {
    const { title, disabled } = this.props;
    const { menuList } = this.state;
    const dropMenu = (
      <Menu onClick={this.handleClick}>
        {menuList.map(item => {
          return <Menu.Item key={item.id}>{item.invoiceTypeName}</Menu.Item>;
        })}
      </Menu>
    );
    return (
      <Dropdown
        overlay={dropMenu}
        trigger={['click']}
        disabled={!!disabled}
        getPopupContainer={trigger => trigger.parentNode}
      >
        <Button>
          {title}
          <Icon type="down" />
        </Button>
      </Dropdown>
    );
  }
}
SelectType.propTypes = {
  title: PropTypes.string, //标题
};

SelectType.defaultProps = {
  title: '手动录入发票',
};

export default SelectType;
