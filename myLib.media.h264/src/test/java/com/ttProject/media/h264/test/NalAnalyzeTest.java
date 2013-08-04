package com.ttProject.media.h264.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.media.h264.NalAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * nal構造を解析する動作テスト
 * @author taktod
 *
 */
public class NalAnalyzeTest {
	@Test
	public void test() throws Exception {
		String data = "000000016764001EACB20283F42000000300200000FA01E2C5C90000000168CBCF2C0000010605FFFF6CDC45E9BDE6D948B7962CD820D923EEEF78323634202D20636F726520313232207233353936382B36324D2032616132313534202D20482E3236342F4D5045472D342041564320636F646563202D20436F70796C65667420323030332D32303132202D20687474703A2F2F7777772E766964656F6C616E2E6F72672F783236342E68746D6C202D206F7074696F6E733A2063616261633D30207265663D33206465626C6F636B3D313A303A3020616E616C7973653D3078333A3078313333206D653D686578207375626D653D35207073793D31207073795F72643D312E30303A302E3030206D697865645F7265663D31206D655F72616E67653D3136206368726F6D615F6D653D31207472656C6C69733D31203878386463743D312063716D3D3020646561647A6F6E653D32312C313120666173745F70736B69703D31206368726F6D615F71705F6F66667365743D3020746872656164733D3620736C696365645F746872656164733D30206E723D3020646563696D6174653D3120696E7465726C616365643D3020626C757261795F636F6D7061743D3020636F6E73747261696E65645F696E7472613D3020626672616D65733D3020776569676874703D32206B6579696E743D3330206B6579696E745F6D696E3D3136207363656E656375743D343020696E7472615F726566726573683D302072635F6C6F6F6B61686561643D33302072633D616272206D62747265653D3120626974726174653D3235302072617465746F6C3D312E302071636F6D703D302E36302071706D696E3D31302071706D61783D3330207170737465703D342069705F726174696F3D312E34302061713D313A312E3030008000000165888411EFFFF8783E2800086FF93939393939393939393939393939393939393AEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBDFFF426B0F7001589A0D48225A83D5EC0E88C2D844B47E547FFFDC3A488C0187280A3738749190030C5A836D5F00000FC0C624747E38F847D0DC003C393DECC5299FFF634375FE4FDB27BDAFC4F81A085FF00162FCBC84E4073C71FAEB015965810F79A46A2DFB9EB5F755FFFDE073483528A94B4661FFEF3F75F4D3DF7680F02FFFCE28489EC7E23655BFFF0631EEF854BF84FDB89A0D48225A83D5E038452110BA10FC5FBF832000803A107BAE1009D902F801512220041CD2C1423BCE0251A192D162D4D457FBEFC0019B44999B2122AA781337E19090A054599EFFE076A85638CC763FDFCFE3461CD6B322E72FF013BDBC7932E4DDE0EDCFFC31BFA216919554513CE4370021AA55B4A4965761B0C003C7F7064525293D2087CF84226DA439731801F9B666B8C11C216947FF830C3024BAF4C2A0B41C9D5FBFA0B058842AF280E847F7CB13C1F5B1F7C05659700FA28473EF34B56BEE1FFFFF011B44999B2122AA6885A46555144F390DDA26C6FC841D1A83C8F7FFEA68D232D8951D70C1389CD8C573756BAD4690684152A41EAF71C63DAF466F1DFFB14A555537FFFFE06F83159FA986013BE0086A956D292595D860F002A244400839A58204779C04A34325A2C5A9BD5FEFB9B666B8C114216957FF830FE02CDF8642428151664FB8B542B1C663A9FEFE587F4FC1043813BDBC68D1726EF7DF9E18784BC005B20D042B5292DA3BCFB02D4886A52DA2B9DFEF7DF9C336A6F801C13220041CB3036D4580008058253002CA69C770FABFFE002B3230F0886A506ED40845B2369B22D5A540E7E7F3D11DFDF7DB5985863E231A7FF9F0562683520896A8F57AFC3BB68D17E00166B9328D5B3A37E21BF000B2E3DB9DB1AF7882ED6F26C20C86204E4F57B8786A74EA5E095E725976268421617B7BF780033689333642455402FA214E573BAAEFFA81AE89197AABBFEFB75F10F85F000B6459ED900483BDDFB014823E72DA1F0A77BBF001E88C084D34D6B415CFB1A02422287C496963FFDF432000803A5900A09910020E5981B6A82B1341A9844B507ABDFF621C00E86E417FC010218B884B1083AB011A4699186376E96EF7A64C245C8E3918F00637909D765E9917FC29E9C8D192F7364D19321A22A794244F63F11F32CDFE8A735A349AF892A892554198000821E6265AFDADF84FE88E4C64C1E4B5621FBFD34257AB5CA78F983890262D2B623D75D75D75EB4497FF84B0007E3314B897AE393FA1DBD900227110270A9BB5D0E8B0D002CA51415BC3A78B7FF094300070BDA979EB91F946FE87A1A75C190CA3D5C95FBED7C28BDB4D55FFBEFF8704FFF094382644008396606DA83A48861CB28C0DB57F818C398D1307B2579BFEFFD70B1FFF09400A133200618D2C1423BCEE383D84674E13B946FDE1E294912A927EFBC086F2344C9B8D6F0E32E21FF84BC1344AAD3A32AF6FEE03F224E78E57E41BF830C30F007939403247017D67E76CCFFF84B41A55843094D7BFEFF8DBB3C5B6AC44DEFFE5BC9A2326E35B8268955B74655E4FE0C38B0B87FE12F01F91273C777E41BF830E00509990030C6960811DE771C1EC233A709DCA2FEF0E6F8043F87BCAA492A955FF06181C0080382995884C3FF0960700200E0A6400A92230061CA028DCFC0504C8801072CC0DB50109E499A26F25BC3A3067FFE12A82009014B305C8800200E632FC1923D00F961517FBF05FD2C003FF09430E92220061CB3036D5F8648841CB2CD0DB502FE8FC3FE1280A0991002052CC0DB53A13230041CA028DCFFFC0010FAE1B6AB77467E9210870371FFE12E2C8C3D444353D0FFFFBB59F2A27B25DB5FDFFE1C008038E41FF878109E0002247EBAEBAEBAEBAEBAEBAEBAEBAEBBEFAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAF0";
		//                     6764001EACB20283F42000000300200000FA01E2C5C9                      0605FFFF6CDC45E9BDE6D948B7962CD820D923EEEF78323634202D20636F726520313232207233353936382B36324D2032616132313534202D20482E3236342F4D5045472D342041564320636F646563202D20436F70796C65667420323030332D32303132202D20687474703A2F2F7777772E766964656F6C616E2E6F72672F783236342E68746D6C202D206F7074696F6E733A2063616261633D30207265663D33206465626C6F636B3D313A303A3020616E616C7973653D3078333A3078313333206D653D686578207375626D653D35207073793D31207073795F72643D312E30303A302E3030206D697865645F7265663D31206D655F72616E67653D3136206368726F6D615F6D653D31207472656C6C69733D31203878386463743D312063716D3D3020646561647A6F6E653D32312C313120666173745F70736B69703D31206368726F6D615F71705F6F66667365743D3020746872656164733D3620736C696365645F746872656164733D30206E723D3020646563696D6174653D3120696E7465726C616365643D3020626C757261795F636F6D7061743D3020636F6E73747261696E65645F696E7472613D3020626672616D65733D3020776569676874703D32206B6579696E743D3330206B6579696E745F6D696E3D3136207363656E656375743D343020696E7472615F726566726573683D302072635F6C6F6F6B61686561643D33302072633D616272206D62747265653D3120626974726174653D3235302072617465746F6C3D312E302071636F6D703D302E36302071706D696E3D31302071706D61783D3330207170737465703D342069705F726174696F3D312E34302061713D313A312E30300080
		//                                                                         68CBCF2C                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            65888411EFFFF8783E2800086FF93939393939393939393939393939393939393AEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBDFFF426B0F7001589A0D48225A83D5EC0E88C2D844B47E547FFFDC3A488C0187280A3738749190030C5A836D5F00000FC0C624747E38F847D0DC003C393DECC5299FFF634375FE4FDB27BDAFC4F81A085FF00162FCBC84E4073C71FAEB015965810F79A46A2DFB9EB5F755FFFDE073483528A94B4661FFEF3F75F4D3DF7680F02FFFCE28489EC7E23655BFFF0631EEF854BF84FDB89A0D48225A83D5E038452110BA10FC5FBF832000803A107BAE1009D902F801512220041CD2C1423BCE0251A192D162D4D457FBEFC0019B44999B2122AA781337E19090A054599EFFE076A85638CC763FDFCFE3461CD6B322E72FF013BDBC7932E4DDE0EDCFFC31BFA216919554513CE4370021AA55B4A4965761B0C003C7F7064525293D2087CF84226DA439731801F9B666B8C11C216947FF830C3024BAF4C2A0B41C9D5FBFA0B058842AF280E847F7CB13C1F5B1F7C05659700FA28473EF34B56BEE1FFFFF011B44999B2122AA6885A46555144F390DDA26C6FC841D1A83C8F7FFEA68D232D8951D70C1389CD8C573756BAD4690684152A41EAF71C63DAF466F1DFFB14A555537FFFFE06F83159FA986013BE0086A956D292595D860F002A244400839A58204779C04A34325A2C5A9BD5FEFB9B666B8C114216957FF830FE02CDF8642428151664FB8B542B1C663A9FEFE587F4FC1043813BDBC68D1726EF7DF9E18784BC005B20D042B5292DA3BCFB02D4886A52DA2B9DFEF7DF9C336A6F801C13220041CB3036D4580008058253002CA69C770FABFFE002B3230F0886A506ED40845B2369B22D5A540E7E7F3D11DFDF7DB5985863E231A7FF9F0562683520896A8F57AFC3BB68D17E00166B9328D5B3A37E21BF000B2E3DB9DB1AF7882ED6F26C20C86204E4F57B8786A74EA5E095E725976268421617B7BF780033689333642455402FA214E573BAAEFFA81AE89197AABBFEFB75F10F85F000B6459ED900483BDDFB014823E72DA1F0A77BBF001E88C084D34D6B415CFB1A02422287C496963FFDF432000803A5900A09910020E5981B6A82B1341A9844B507ABDFF621C00E86E417FC010218B884B1083AB011A4699186376E96EF7A64C245C8E3918F00637909D765E9917FC29E9C8D192F7364D19321A22A794244F63F11F32CDFE8A735A349AF892A892554198000821E6265AFDADF84FE88E4C64C1E4B5621FBFD34257AB5CA78F983890262D2B623D75D75D75EB4497FF84B0007E3314B897AE393FA1DBD900227110270A9BB5D0E8B0D002CA51415BC3A78B7FF094300070BDA979EB91F946FE87A1A75C190CA3D5C95FBED7C28BDB4D55FFBEFF8704FFF094382644008396606DA83A48861CB28C0DB57F818C398D1307B2579BFEFFD70B1FFF09400A133200618D2C1423BCEE383D84674E13B946FDE1E294912A927EFBC086F2344C9B8D6F0E32E21FF84BC1344AAD3A32AF6FEE03F224E78E57E41BF830C30F007939403247017D67E76CCFFF84B41A55843094D7BFEFF8DBB3C5B6AC44DEFFE5BC9A2326E35B8268955B74655E4FE0C38B0B87FE12F01F91273C777E41BF830E00509990030C6960811DE771C1EC233A709DCA2FEF0E6F8043F87BCAA492A955FF06181C0080382995884C3FF0960700200E0A6400A92230061CA028DCFC0504C8801072CC0DB50109E499A26F25BC3A3067FFE12A82009014B305C8800200E632FC1923D00F961517FBF05FD2C003FF09430E92220061CB3036D5F8648841CB2CD0DB502FE8FC3FE1280A0991002052CC0DB53A13230041CA028DCFFFC0010FAE1B6AB77467E9210870371FFE12E2C8C3D444353D0FFFFBB59F2A27B25DB5FDFFE1C008038E41FF878109E0002247EBAEBAEBAEBAEBAEBAEBAEBAEBBEFAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAEBAF0
		ByteBuffer h264Frame = HexUtil.makeBuffer(data);
		// 読み込みモードでh264Frameができあがっている。あとはここから、nalに分解してデータを取得するだけ。
		IReadChannel dataChannel = new ByteReadChannel(h264Frame);
		NalAnalyzer analyzer = new NalAnalyzer();
		analyzer.analyze(dataChannel);
//		System.out.println(h264F);
	}
}
