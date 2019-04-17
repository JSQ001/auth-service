import React, { Component } from 'react';
import { Tree, Icon, Menu, Dropdown, message, Modal } from 'antd';
import 'styles/enterprise-manage/org-structure/org-component/org-tree.scss';
import service from '../../work-group-definition-service';

const TreeNode = Tree.TreeNode;

class WorkTeamTree extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount = () => {};

  treeNodeStopPropagation = e => {
    e.stopPropagation();
  };
  //渲染工作组树节点
  renderTreeNodes = treeDataList => {
    if (JSON.stringify(treeDataList) !== '[]') {
      return treeDataList.map(treeValue => {
        //存在子节点
        if (treeValue.children) {
          if (treeValue.enabled) {
            //节点可用
            return (
              <TreeNode
                title={this.renderTreeNodeTitle(treeValue)}
                key={treeValue.id}
                dataRef={treeValue}
                className="org-dep-node"
              >
                {this.renderTreeNodes(treeValue.children)}
              </TreeNode>
            );
          } else {
            //节点置灰不可用
            return (
              <TreeNode
                title={this.renderTreeNodeTitle(treeValue, 'disabled')}
                // disabled
                key={treeValue.id}
                dataRef={treeValue}
                className="org-dep-node"
              >
                {this.renderTreeNodes(treeValue.children)}
              </TreeNode>
            );
          }
          //节点禁用
        } else {
          if (treeValue.enabled) {
            return (
              <TreeNode
                className="org-dep-node"
                title={this.renderTreeNodeTitle(treeValue)}
                key={treeValue.id}
                dataRef={treeValue}
              />
            );
          } else {
            return (
              <TreeNode
                className="org-dep-node"
                // disabled
                title={this.renderTreeNodeTitle(treeValue, 'disabled')}
                key={treeValue.id}
                dataRef={treeValue}
              />
            );
          }
        }
      });
    }
  };

  //渲染树节点title属性值
  renderTreeNodeTitle = (value, isDisabled) => {
    let titleDom = '';
    if (isDisabled == 'disabled') {
      titleDom = (
        <span
          className="org-dep-node-title"
          style={{ color: 'rgba(0,0,0,.3)', lineHeight: '27px' }}
        >
          {value.workTeamName}
        </span>
      );
    } else
      titleDom = (
        <span className="org-dep-node-title" style={{ lineHeight: '27px' }}>
          {value.workTeamName}
        </span>
      );

    return (
      <div className="org-dep-node-title-wrap">
        {titleDom}
        <span
          className="org-dep-node-set"
          onClick={event => {
            this.treeNodeStopPropagation(event);
          }}
        >
          {this.renderDropdown(value)}
        </span>
      </div>
    );
  };

  //渲染下拉按钮
  renderDropdown = item => {
    return (
      <Dropdown overlay={this.renderTreeNodeTitleMeun(item)} trigger={['click']}>
        <a
          className="ant-dropdown-link"
          href="#"
          style={
            item.enabled
              ? { verticalAlign: 'middle' }
              : { verticalAlign: 'middle', color: 'rgba(0,0,0,.3)' }
          }
        >
          <Icon type="bars" />
        </a>
      </Dropdown>
    );
  };
  renderTreeNodeTitleMeun = item => {
    //启用状态的menu
    if (item.enabled) {
      return (
        <Menu>
          <Menu.Item key="0">
            <span
              onClick={event => {
                this.handlePeersNewTeam(event, item);
              }}
            >
              {/* 新建平级工作组 */}
              {this.$t('workbench.workTeam.peer.new')}
            </span>
          </Menu.Item>
          <Menu.Item key="1">
            <span
              onClick={event => {
                this.handleNewLowerLevelTeam(event, item);
              }}
            >
              {/* 新建下级工作组 */}
              {this.$t('workbench.workTeam.lowerLevel.new')}
            </span>
          </Menu.Item>
          <Menu.Item key="2">
            <span
              onClick={event => {
                this.handleDisableOrNoCurTeam(event, item, false);
              }}
            >
              {/* 禁用当前工作组 */}
              {this.$t('workbench.workTeam.disabled')}
            </span>
          </Menu.Item>
          <Menu.Item key="3">
            <span
              onClick={event => {
                this.handleDelCurTeam(event, item);
              }}
            >
              {/* 删除当前工作组 */}
              {this.$t('workbench.workTeam.delete')}
            </span>
          </Menu.Item>
        </Menu>
      );
    } else {
      //禁用状态下的menu
      return (
        <Menu>
          <Menu.Item key="4">
            <span
              onClick={event => {
                this.handleDisableOrNoCurTeam(event, item, true);
              }}
            >
              {/* 启用当前工作组 */}
              {this.$t('workbench.workTeam.enabled')}
            </span>
          </Menu.Item>
          <Menu.Item key="5">
            <span
              onClick={event => {
                this.handleDelCurTeam(event, item);
              }}
            >
              {/* 删除当前工作组 */}
              {this.$t('workbench.workTeam.delete')}
            </span>
          </Menu.Item>
        </Menu>
      );
    }
  };

  //新建平级工作组
  handlePeersNewTeam = (e, item) => {
    this.props.handleNewWorkTeam('peers', item);
  };

  //新建下级工作组
  handleNewLowerLevelTeam = (e, item) => {
    this.props.handleNewWorkTeam('lowerLevel', item);
  };

  //禁用或启用当前工作组
  handleDisableOrNoCurTeam = (e, item, flag) => {
    let params = { ...item };
    let messageValue =
      flag == true
        ? this.$t('workbench.workTeam.enabled.opt.success')
        : this.$t('workbench.workTeam.disabled.opt.success');
    params.enabled = flag;
    service
      .editWorkTeamValue(params)
      .then(res => {
        message.success(messageValue);
        this.props.getWorkGroupValueList('query', true);
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //删除当前工作组
  /**
   * 1.删有子组的则return，阻止接口调用，
   * 2.删被选中的，则清空selectedKey，当调用接口全查工作组的时候，将selectedKey重新设置成第一条
   * 3.删其他的，则直接调用
   *  */
  handleDelCurTeam = (e, item) => {
    if (item.children && item.children.length > 0) {
      Modal.warning({
        title: this.$t('org.tips'),
        content: this.$t('workbench.workTeam.have.subgroups'), //'该工作组存在子组'
      });
      return;
    }
    service
      .deleteWorkTeamValue(item.id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        if (item.id == this.props.selectedKeys[0]) {
          //当前工作组是被选中的情况下：清除selectedKey
          this.props.changeSelectedKeys();
        } else this.props.getWorkGroupValueList('query', true);
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    const {
      selectedKeys,
      expandedKeys,
      autoExpandParent,
      onSelect,
      onExpand,
      treeData,
    } = this.props;

    return (
      <div className="org-structure-tree">
        <Tree
          selectedKeys={selectedKeys}
          expandedKeys={expandedKeys}
          autoExpandParent={autoExpandParent}
          onSelect={onSelect}
          onExpand={onExpand}
          switcherIcon={
            <Icon
              type="caret-down"
              style={{ color: '#00ABEA', fontSize: '24px', lineHeight: '27px' }}
            />
          }
        >
          {this.renderTreeNodes(treeData)}
        </Tree>
      </div>
    );
  }
}

export default WorkTeamTree;
