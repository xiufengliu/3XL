/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlclient.common;

public class SocketCmd {
	
	/*MSG_OPEN_CONFIG, 
	MSG_GEN_SCHEMA,
	MSG_LOAD,
	MSG_QUERY,
	MSG_TRAN_CONFIG,
	MSG_TRAN_ONTOLOGY,
	
	
	MSG_ERROR,
	MSG_END,
	
	MSG_IS_ALIVE,
	MSG_I_AM_ALIVE,
	
	
	MSG_BYE,
	MSG_ONTOLOGY_RECV*/
	
	public static final String MSG_OPEN_CONFIG = "OpenConfig";
	public static final String MSG_SAVE_CONFIG = "SaveConfig";
	
	public static final String MSG_SAVE_ONT = "SaveOnt";
	public static final String MSG_ONT_IS_SAVED = "OntIsSaved";
	public static final String MSG_GEN_SCHEMA = "GenSchema";
	public static final String MSG_LOAD = "Load";
	public static final String MSG_QUERY = "Query";
	public static final String MSG_END = "End";
	public static final String MSG_ERROR = "Error";
	public static final String MSG_EOT = "---END OF QUERY---";
	
	
	
	public static final String MSG_TRAN_CONFIG = "TransmitConfig";
	public static final String MSG_TRAN_ONTOLOGY = "TransmitOntology";
	public static final String MSG_IS_ALIVE = "IsAlive";
	public static final String MSG_I_AM_ALIVE = "IAMAlive";
	public static final String MSG_BYE = "Bye";
	public static final String MSG_ONTOLOGY_RECV = "OntRecv";
	
	
	


	
}
