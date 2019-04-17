import React from 'react';
import { Tree, Modal, message, Spin, Icon } from 'antd';
import service from './service';

class SelectMenus extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      checkedKeys: [],
      treeData: [],
      defaultIds: [],
      checkedIds: [],
      loading: true,
      saveLoading: false,
    };
    this.group = {};
    this.functionGroup = {};
  }

  componentDidMount() {
    this.getList();
  }

  renderTreeNodes = data => {
    return data.map(item => {
      const isFunction = !!item.functionId;
      const title = isFunction ? item.functionName : item.contentName;
      return (
        <Tree.TreeNode
          dataRef={item}
          title={title}
          key={isFunction ? item.functionId : item.contentId}
          icon={<Icon type={isFunction ? 'file' : 'folder'} />}
        >
          {item.children && this.renderTreeNodes(item.children)}
        </Tree.TreeNode>
      );
    });
  };

  onCheck = (checkedKeys, e) => {
    const checkedIds = e.checkedNodes
      .map(node => {
        if (node.props.dataRef.functionId) {
          return node.props.dataRef.functionId;
        }
        return null;
      })
      .filter(item => item);

    this.setState({ checkedKeys, checkedIds });
  };

  handleOk = async () => {
    const { defaultIds, checkedIds } = this.state;
    const { roleId } = this.props;
    const adds = checkedIds
      .map(o => {
        return defaultIds.indexOf(o) >= 0 ? null : o;
      })
      .filter(item => item);
    const removes = defaultIds
      .map(o => {
        return checkedIds.indexOf(o) >= 0 ? null : o;
      })
      .filter(item => item);

    const addList = adds.map(o => ({
      roleId,
      functionId: o,
      flag: true,
    }));

    const removeList = removes.map(o => ({
      roleId,
      functionId: o,
      flag: false,
    }));
    try {
      this.setState({ saveLoading: true });
      await service.assignMenus([...addList, ...removeList]);
      this.setState({ saveLoading: false });
      message.success('分配成功！');
      this.handleCancel();
    } catch (err) {
      this.setState({ saveLoading: false });
      message.error(err.response.data.message);
    }
  };

  handleCancel = () => {
    const { onCancel } = this.props;
    onCancel && onCancel();
  };

  getList = async () => {
    const { roleId } = this.props;
    const { contentFunctionDTOList, functionIdList } = await service.getAllowedMenu(roleId);

    this.group = this.groupByParent(contentFunctionDTOList);
    const root = [...this.group['root']];
    this.getChildren(root);
    const funcList = contentFunctionDTOList.filter(o => !o.contentId);
    this.setState({
      treeData: [...root, ...funcList],
      checkedKeys: [...functionIdList],
      checkedIds: [...functionIdList],
      defaultIds: [...functionIdList],
      loading: false,
    });
  };

  getChildren = (data = []) => {
    data.map(item => {
      if (item.functionId) return;
      item.children = [
        ...(this.group[item.contentId] || []),
        ...(this.functionGroup[item.contentId] || []),
      ];
      this.getChildren(item.children);
    });
  };

  groupByParent = data => {
    return data.reduce((temp, item) => {
      if (!item.parentId && !item.functionId) {
        if (temp['root']) {
          temp['root'].push(item);
        } else {
          temp['root'] = [item];
        }
      } else {
        if (item.functionId) {
          if (this.functionGroup[item.contentId]) {
            this.functionGroup[item.contentId].push(item);
          } else {
            this.functionGroup[item.contentId] = [item];
          }
        } else {
          if (temp[item.parentId]) {
            temp[item.parentId].push(item);
          } else {
            temp[item.parentId] = [item];
          }
        }
      }
      return temp;
    }, {});
  };

  render() {
    const { checkedKeys, treeData, loading, saveLoading } = this.state;
    const { visible } = this.props;

    return (
      <Modal
        confirmLoading={saveLoading}
        title="选择菜单"
        visible={visible}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        bodyStyle={{ height: '60vh', overflow: 'auto' }}
      >
        <Spin spinning={loading}>
          <Tree
            checkable
            autoExpandParent
            showIcon
            onCheck={this.onCheck}
            checkedKeys={checkedKeys}
          >
            {this.renderTreeNodes(treeData)}
          </Tree>
        </Spin>
      </Modal>
    );
  }
}

export default SelectMenus;
